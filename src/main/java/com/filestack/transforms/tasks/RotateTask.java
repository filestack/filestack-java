package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class RotateTask extends ImageTransformTask {

  // Constructor made package-private because this task cannot be used with default options
  RotateTask() {
    super("rotate");
  }

  public static class Builder {
    private RotateTask rotateTask;

    public Builder() {
      this.rotateTask = new RotateTask();
    }

    public Builder deg(int deg) {
      rotateTask.addOption("deg", deg);
      return this;
    }

    // For setting degree to "exif"
    public Builder deg(String deg) {
      rotateTask.addOption("deg", deg);
      return this;
    }

    public Builder exif(boolean exif) {
      rotateTask.addOption("exif", exif);
      return this;
    }

    public Builder background(String background) {
      rotateTask.addOption("background", background);
      return this;
    }

    public RotateTask build() {
      return rotateTask;
    }
  }
}
