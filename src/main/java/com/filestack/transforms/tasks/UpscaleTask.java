package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class UpscaleTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public UpscaleTask() {
    super("upscale");
  }

  public static class Builder {
    private UpscaleTask upscaleTask;

    public Builder() {
      this.upscaleTask = new UpscaleTask();
    }

    public Builder upscale(boolean upscale) {
      upscaleTask.addOption("upscale", upscale);
      return this;
    }

    public Builder noise(String noise) {
      upscaleTask.addOption("noise", noise);
      return this;
    }

    public Builder style(String style) {
      upscaleTask.addOption("style", style);
      return this;
    }

    public UpscaleTask build() {
      return upscaleTask;
    }
  }
}
