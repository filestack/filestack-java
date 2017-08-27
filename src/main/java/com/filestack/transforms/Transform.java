package com.filestack.transforms;

import com.filestack.FileLink;
import com.filestack.FilestackClient;
import com.filestack.Security;
import com.filestack.util.FsService;

import java.util.ArrayList;

import okhttp3.HttpUrl;

/**
 * Base class for file transformations and conversions.
 */
public class Transform {
  String apiKey;
  String source;
  Security security;

  ArrayList<TransformTask> tasks;

  FsService fsService;

  Transform(FilestackClient fsClient, String url) {
    this(fsClient, null, url);
  }

  Transform(FileLink fileLink) {
    this(null, fileLink, null);
  }

  Transform(FilestackClient fsClient, FileLink fileLink, String url) {
    if (fsClient != null) {
      this.apiKey = fsClient.getApiKey();
      this.source = url;
      this.fsService = fsClient.getFsService();
    } else {
      this.source = fileLink.getHandle();
      this.fsService = fileLink.getFsService();
    }

    this.tasks = new ArrayList<>();

    Security security = fsClient != null ? fsClient.getSecurity() : fileLink.getSecurity();
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
   * Includes the related {@link FileLink FileLink's} policy and signature.
   *
   * @return transformation URL
   */
  public String url() {
    String tasksString = getTasksString();
    HttpUrl httpUrl;

    if (apiKey != null) {
      httpUrl = fsService.transformExt(apiKey, tasksString, source).request().url();
    } else {
      httpUrl = fsService.transform(tasksString, source).request().url();
    }

    // When building the request we add a / between tasks
    // Because that entire task string is added as a single path variable, the / is URL encoded
    // That's a little confusing so we're replacing "%2F" with "/" for a more expected URL
    return httpUrl.toString().replace("%2F", "/");
  }
}
