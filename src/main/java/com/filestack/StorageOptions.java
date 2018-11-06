package com.filestack;

import com.filestack.internal.Util;
import com.filestack.transforms.TransformTask;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Locale;

/** Configure storage options for uploads and transformation stores. */
public class StorageOptions implements Serializable {
  private static final String DEFAULT_MIME_TYPE = "application/octet-stream";
  private static final String DEFAULT_LOCATION = "s3";
  private static final String DEFAULT_FILENAME_TEMPLATE = "java-sdk-upload-%d";

  private String access;
  private Boolean base64Decode;
  private String container;
  private String filename;
  private String location;
  private String mimeType;
  private String path;
  private String region;

  // Private to enforce use of the builder
  private StorageOptions() { }

  public String getFilename() {
    return filename;
  }

  public String getMimeType() {
    return mimeType;
  }

  /** Get these options as a task. */
  public TransformTask getAsTask() {
    TransformTask task = new TransformTask("store");
    addToTask(task);
    return task;
  }

  /** Add these options to an existing task. */
  public void addToTask(TransformTask task) {
    task.addOption("access", access);
    task.addOption("base64decode", base64Decode);
    task.addOption("container", container);
    task.addOption("filename", filename);
    task.addOption("location", location);
    task.addOption("path", path);
    task.addOption("region", region);
  }

  /** Get these options as JSON to use for cloud integrations. */
  public JsonObject getAsJson() {
    JsonObject json = new JsonObject();
    addToJson(json, "access", access);
    addToJson(json, "container", container);
    addToJson(json, "filename", filename);
    addToJson(json, "location", location);
    addToJson(json, "path", path);
    addToJson(json, "region", region);
    return json;
  }

  public Builder newBuilder() {
    return new Builder(this);
  }


  private static void addToJson(JsonObject json, String key, @Nullable String value) {
    if (value != null) {
      json.addProperty(key, value);
    }
  }

  public String getAccess() {
    return access;
  }

  public Boolean getBase64Decode() {
    return base64Decode;
  }

  public String getContainer() {
    return container;
  }

  public String getLocation() {
    return location;
  }

  public String getPath() {
    return path;
  }

  public String getRegion() {
    return region;
  }

  public static class Builder {
    // Setting these is optional
    private Boolean base64Decode;
    private String access;
    private String container;
    private String filename;
    private String location;
    private String mimeType;
    private String path;
    private String region;

    public Builder() { }

    /** Create a new builder using an existing options config. */
    public Builder(StorageOptions existing) {
      access = existing.access;
      base64Decode = existing.base64Decode;
      container = existing.container;
      filename = existing.filename;
      location = existing.location;
      path = existing.path;
      region = existing.region;
      mimeType = existing.mimeType;
    }

    public Builder access(String access) {
      this.access = access;
      return this;
    }

    public Builder base64Decode(boolean base64Decode) {
      this.base64Decode = base64Decode;
      return this;
    }

    public Builder container(String container) {
      this.container = container;
      return this;
    }

    public Builder filename(String filename) {
      this.filename = filename;
      return this;
    }

    public Builder location(String location) {
      this.location = location;
      return this;
    }

    public Builder path(String path) {
      this.path = path;
      return this;
    }

    public Builder region(String region) {
      this.region = region;
      return this;
    }

    public Builder mimeType(String contentType) {
      this.mimeType = contentType;
      return this;
    }

    /**
     * Builds new {@link StorageOptions}.
     */
    public StorageOptions build() {
      StorageOptions building = new StorageOptions();

      building.access = access;
      building.base64Decode = base64Decode;
      building.container = container;
      building.filename = filename;
      if (Util.isNullOrEmpty(filename)) {
        building.filename = generateFilename();
      }
      building.location = location;
      if (location == null) {
        building.location = DEFAULT_LOCATION;
      }
      building.mimeType = mimeType;
      if (mimeType == null) {
        building.mimeType = DEFAULT_MIME_TYPE;
      }
      building.path = path;
      building.region = region;

      return building;
    }

    private static String generateFilename() {
      long seconds = System.currentTimeMillis() / 1000L;
      return String.format(Locale.ROOT, DEFAULT_FILENAME_TEMPLATE, seconds);
    }
  }
}
