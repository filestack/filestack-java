package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class WatermarkTask extends ImageTransformTask {

  // Constructor made package-private because this task cannot be used with default options
  WatermarkTask() {
    super("watermark");
  }

  public static class Builder {
    private WatermarkTask watermarkTask;

    public Builder() {
      this.watermarkTask = new WatermarkTask();
    }

    public Builder file(String handle) {
      watermarkTask.addOption("file", handle);
      return this;
    }

    public Builder size(int size) {
      watermarkTask.addOption("size", size);
      return this;
    }

    public Builder position(String position) {
      watermarkTask.addOption("position", position);
      return this;
    }

    public Builder position(String first, String second) {
      watermarkTask.addOption("position", new String[] {first, second});
      return this;
    }

    public WatermarkTask build() {
      return watermarkTask;
    }
  }
}
