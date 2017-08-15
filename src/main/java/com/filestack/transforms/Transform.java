package com.filestack.transforms;

import com.filestack.FileLink;
import com.filestack.FilestackClient;
import com.filestack.Security;
import com.filestack.util.FilestackService;
import com.filestack.util.Networking;

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

  FilestackService.Process processService;

  Transform(FilestackClient fsClient, String source) {
    this(fsClient, source, null);
  }

  Transform(FileLink fileLink) {
    this(null, null, fileLink);
  }

  Transform(FilestackClient fsClient, String source, FileLink fileLink) {
    if (fsClient != null) {
      this.apiKey = fsClient.getApiKey();
      this.source = source;
    } else {
      this.source = fileLink.getHandle();
    }

    this.tasks = new ArrayList<>();
    this.processService = Networking.getProcessService();

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
      httpUrl = processService.getExternal(apiKey, tasksString, source).request().url();
    } else {
      httpUrl = processService.get(tasksString, source).request().url();
    }

    // When building the request we add a / between tasks
    // Because that entire task string is added as a single path variable, the / is URL encoded
    // That's a little confusing so we're replacing "%2F" with "/" for a more expected URL
    return httpUrl.toString().replace("%2F", "/");
  }
}
