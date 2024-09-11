package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class ResizeTask extends ImageTransformTask {

  // Constructor made package-private because this task cannot be used with default options
  ResizeTask() {
    super("resize");
  }

  public static class Builder {
    private ResizeTask resizeTask;

    public Builder() {
      this.resizeTask = new ResizeTask();
    }

    public Builder width(int width) {
      resizeTask.addOption("width", width);
      return this;
    }

    public Builder height(int height) {
      resizeTask.addOption("height", height);
      return this;
    }

    public Builder fit(String fit) {
      resizeTask.addOption("fit", fit);
      return this;
    }

    public Builder align(String align) {
      resizeTask.addOption("align", align);
      return this;
    }

    public Builder align(String first, String second) {
      resizeTask.addOption("align", new String[] {first, second});
      return this;
    }

    public ResizeTask build() {
      return resizeTask;
    }
  }
}
