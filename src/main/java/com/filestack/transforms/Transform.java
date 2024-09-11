package org.filestack.transforms;

import org.filestack.Config;
import org.filestack.FileLink;
import org.filestack.HttpException;
import org.filestack.internal.CdnService;
import org.filestack.internal.Response;
import org.filestack.internal.Util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.reactivex.Single;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Base class for file transformations and conversions.
 */
public class Transform {
  protected final Config config;
  protected final CdnService cdnService;
  protected final String source;
  protected final boolean isExternal;

  protected final ArrayList<TransformTask> tasks = new ArrayList<>();

  protected Transform(CdnService cdnService, Config config, String source, boolean isExternal) {
    this.cdnService = cdnService;
    this.config = config;
    this.source = source;
    this.isExternal = isExternal;

    if (config.hasSecurity()) {
      TransformTask securityTask = new TransformTask("security");
      securityTask.addOption("policy", config.getPolicy());
      securityTask.addOption("signature", config.getSignature());
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
   * Includes the related {@link FileLink FileLink's} policy and signature.
   *
   * @return transformation URL
   */
  public String url() {
    String tasksString = getTasksString();
    HttpUrl httpUrl;

    if (isExternal) {
      httpUrl = cdnService.transformExtUrl(config.getApiKey(), tasksString, source);
    } else {
      httpUrl = cdnService.transformUrl(tasksString, source);
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

    if (isExternal) {
      response = cdnService.transformExt(config.getApiKey(), tasksString, source);
    } else {
      response = cdnService.transform(tasksString, source);
    }

    Util.checkResponseAndThrow(response);
    return response.getData();
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
    });
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
    });
  }
}
