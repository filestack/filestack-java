package com.filestack.transforms;

import com.filestack.FsClient;
import com.filestack.FsFile;
import com.filestack.HttpException;
import com.filestack.Security;
import com.filestack.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.reactivex.Single;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Base class for file transformations and conversions.
 */
public class Transform {
  protected final FsClient fsClient;
  protected final String url;
  protected final FsFile fsFile;

  protected final ArrayList<TransformTask> tasks = new ArrayList<>();

  protected Transform(FsFile fsFile) {
    this.fsClient = fsFile.getFsClient();
    this.fsFile = fsFile;
    this.url = null;
    setupSecurity();
  }

  protected Transform(FsClient fsClient, String url) {
    this.fsClient = fsClient;
    this.url = url;
    this.fsFile = null;
    setupSecurity();
  }

  protected void setupSecurity() {
    Security security;

    if (fsFile != null && fsFile.getSecurity() != null) {
      security = fsFile.getSecurity();
    } else {
      security = fsClient.getSecurity();
    }

    if (security != null) {
      TransformTask securityTask = new TransformTask("security");
      securityTask.addOption("policy", security.getPolicy());
      securityTask.addOption("signature", security.getSignature());
      this.tasks.add(securityTask);
    }
  }

  /**
   * Format tasks into single string to insert into request.
   */
  String getTasksString() {
    if (tasks.size() == 0) {
      return "";
    }

    StringBuilder stringBuilder = new StringBuilder();
    for (TransformTask task : tasks) {
      stringBuilder.append(task.toString()).append('/');
    }
    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    return stringBuilder.toString();
  }

  /**
   * Generates a URL of the transformation.
   * Includes the related {@link FsFile FsFile's} policy and signature.
   *
   * @return transformation URL
   */
  public String url() {
    String tasksString = getTasksString();
    HttpUrl httpUrl;

    if (url != null) {
      httpUrl = fsClient.getFsService()
          .cdn()
          .transformExt(fsClient.getApiKey(), tasksString, url)
          .request()
          .url();
    } else {
      httpUrl = fsClient.getFsService()
          .cdn()
          .transform(tasksString, fsFile.getHandle())
          .request()
          .url();
    }

    // When building the request we add a / between tasks
    // Because that entire task string is added as a single path variable, the / is URL encoded
    // That's a little confusing so we're replacing "%2F" with "/" for a more expected URL
    return httpUrl.toString().replace("%2F", "/");
  }

  /**
   * Returns the content of a transformation.
   *
   * @return raw transformation content, streamable
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   */
  public ResponseBody getContent() throws IOException {
    String tasksString = getTasksString();
    Response<ResponseBody> response;

    if (url != null) {
      response = fsClient.getFsService()
          .cdn()
          .transformExt(fsClient.getApiKey(), tasksString, url)
          .execute();
    } else {
      response = fsClient.getFsService()
          .cdn()
          .transform(tasksString, fsFile.getHandle())
          .execute();
    }

    Util.checkResponseAndThrow(response);

    return response.body();
  }

  /**
   * Returns the content of a transformation as JSON.
   *
   * @see #getContent()
   */
  public JsonObject getContentJson() throws IOException {
    ResponseBody body = getContent();

    Gson gson = new Gson();
    return gson.fromJson(body.charStream(), JsonObject.class);
  }

  // Async method wrappers

  /**
   * Asynchronously returns the content of a transformation.
   *
   * @see #getContent()
   */
  public Single<ResponseBody> getContentAsync() {
    return Single.fromCallable(new Callable<ResponseBody>() {
      @Override
      public ResponseBody call() throws Exception {
        return getContent();
      }
    })
        .subscribeOn(fsClient.getSubScheduler())
        .observeOn(fsClient.getObsScheduler());
  }

  /**
   * Asynchronously returns the content of a transformation as JSON.
   *
   * @see #getContent()
   */
  public Single<JsonObject> getContentJsonAsync() {
    return Single.fromCallable(new Callable<JsonObject>() {
      @Override
      public JsonObject call() throws Exception {
        return getContentJson();
      }
    })
        .subscribeOn(fsClient.getSubScheduler())
        .observeOn(fsClient.getObsScheduler());
  }
}
