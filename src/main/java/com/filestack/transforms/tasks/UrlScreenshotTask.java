package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class UrlScreenshotTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public UrlScreenshotTask() {
    super("urlscreenshot");
  }

  public static class Builder {
    private UrlScreenshotTask urlScreenshotTask;

    public Builder() {
      this.urlScreenshotTask = new UrlScreenshotTask();
    }

    public Builder agent(String agent) {
      urlScreenshotTask.addOption("agent", agent);
      return this;
    }

    public Builder mode(String mode) {
      urlScreenshotTask.addOption("mode", mode);
      return this;
    }

    public Builder width(int width) {
      urlScreenshotTask.addOption("width", width);
      return this;
    }

    public Builder height(int height) {
      urlScreenshotTask.addOption("height", height);
      return this;
    }

    public Builder delay(int delay) {
      urlScreenshotTask.addOption("delay", delay);
      return this;
    }

    public UrlScreenshotTask build() {
      return urlScreenshotTask;
    }
  }
}
