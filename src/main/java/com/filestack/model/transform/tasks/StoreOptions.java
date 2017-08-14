package com.filestack.model.transform.tasks;

import com.filestack.model.transform.base.ImageTransformTask;

public class StoreOptions extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public StoreOptions() {
    super("store");
  }

  public static class Builder {
    private StoreOptions storeOptions;

    public Builder() {
      this.storeOptions = new StoreOptions();
    }

    public Builder filename(String filename) {
      storeOptions.addOption("filename", filename);
      return this;
    }

    public Builder location(String location) {
      storeOptions.addOption("location", location);
      return this;
    }

    public Builder path(String path) {
      storeOptions.addOption("path", path);
      return this;
    }

    public Builder container(String container) {
      storeOptions.addOption("container", container);
      return this;
    }

    public Builder region(String region) {
      storeOptions.addOption("region", region);
      return this;
    }

    public Builder access(String access) {
      storeOptions.addOption("access", access);
      return this;
    }

    public Builder base64Decode(boolean base64Decode) {
      storeOptions.addOption("base64decode", base64Decode);
      return this;
    }

    public StoreOptions build() {
      return storeOptions;
    }
  }
}
