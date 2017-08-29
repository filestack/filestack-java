package com.filestack;

import com.filestack.transforms.TransformTask;
import com.filestack.util.Util;
import java.util.HashMap;
import java.util.Map;
import okhttp3.RequestBody;

/** Configure storage options for uploads and transformation stores. */
public class StorageOptions {
  private String access;
  private Boolean base64Decode;
  private String container;
  private String filename;
  private String location;
  private String path;
  private String region;

  public StorageOptions() { }

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
    return map;
  }

  private static void addToMap(Map<String, RequestBody> map, String key, String value) {
    if (value != null) {
      map.put(key, Util.createStringPart(value));
    }
  }

  public static class Builder {
    private String access;
    private Boolean base64Decode;
    private String container;
    private String filename;
    private String location;
    private String path;
    private String region;

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
      building.path = path;
      building.region = region;
      return building;
    }
  }
}
