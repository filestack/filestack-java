package com.filestack.transforms;

import com.filestack.FsClient;
import com.filestack.FsFile;
import com.filestack.HttpResponseException;
import com.filestack.Security;
import com.filestack.util.FsService;
import com.filestack.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Base class for file transformations and conversions.
 */
public class Transform {
  String apiKey;
  String source;
  Security security;

  ArrayList<TransformTask> tasks;

  FsService fsService;

  Transform(FsClient fsClient, String url) {
    this(fsClient, null, url);
  }

  Transform(FsFile fsFile) {
    this(null, fsFile, null);
  }

  Transform(FsClient fsClient, FsFile fsFile, String url) {
    if (fsClient != null) {
      this.apiKey = fsClient.getApiKey();
      this.source = url;
      this.fsService = fsClient.getFsService();
    } else {
      this.source = fsFile.getHandle();
      this.fsService = fsFile.getFsService();
    }

    this.tasks = new ArrayList<>();

    Security security = fsClient != null ? fsClient.getSecurity() : fsFile.getSecurity();
    this.security = security;
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

    if (apiKey != null) {
      httpUrl = fsService.cdn().transformExt(apiKey, tasksString, source).request().url();
    } else {
      httpUrl = fsService.cdn().transform(tasksString, source).request().url();
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
   * @throws HttpResponseException on error response from backend
   * @throws IOException           on network failure
   */
  public ResponseBody getContent() throws IOException {
    String tasksString = getTasksString();
    Response<ResponseBody> response;

    if (apiKey != null) {
      response = fsService.cdn().transformExt(apiKey, tasksString, source).execute();
    } else {
      response = fsService.cdn().transform(tasksString, source).execute();
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
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
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
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
