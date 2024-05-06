package org.filestack;

import org.filestack.internal.Util;
import org.filestack.transforms.TransformTask;
import com.google.gson.JsonObject;
import okhttp3.RequestBody;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** Configure storage options for uploads and transformation stores. */
public class StorageOptions implements Serializable {
  private static final String DEFAULT_FILENAME = "java-sdk-upload-%d";
  private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

  private String access;
  private Boolean base64Decode;
  private String container;
  private String filename;
  private String location;
  private String mimeType;
  private String path;
  private String region;

  // Private to enforce use of the builder
  private StorageOptions() {

  }

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

  /** Get these options as a part map to use for uploads. */
  public Map<String, RequestBody> getAsPartMap() {
    HashMap<String, RequestBody> map = new HashMap<>();
    addToMap(map, "store_access", access);
    addToMap(map, "store_container", container);
    addToMap(map, "store_location", location != null ? location : "s3");
    addToMap(map, "store_path", path);
    addToMap(map, "store_region", region);

    // A name and MIME type must be set for uploads, so we set a default here but not in "build"
    // If we're not using the instance for an upload, we don't want to set these defaults
    if (Util.isNullOrEmpty(filename)) {
      long timestamp = System.currentTimeMillis() / 1000L;
      filename = String.format(DEFAULT_FILENAME, timestamp);
    }

    // There's too many variables in guessing MIME types, esp between platforms
    // Either the user sets it themselves or we use a default
    if (Util.isNullOrEmpty(mimeType)) {
      mimeType = DEFAULT_MIME_TYPE;
    }

    addToMap(map, "filename", filename);
    addToMap(map, "mimetype", mimeType);
    return map;
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

  private static void addToMap(Map<String, RequestBody> map, String key, @Nullable String value) {
    if (value != null) {
      map.put(key, Util.createStringPart(value));
    }
  }

  private static void addToJson(JsonObject json, String key, @Nullable String value) {
    if (value != null) {
      json.addProperty(key, value);
    }
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

    public Builder() {

    }

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
      building.location = location;
      building.mimeType = mimeType;
      building.path = path;
      building.region = region;

      return building;
    }
  }
}
