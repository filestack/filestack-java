package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

import java.util.ArrayList;

public class CollageTask extends ImageTransformTask {

  // Constructor made package-private because this task cannot be used with default options
  CollageTask() {
    super("collage");
  }

  public static class Builder {
    private CollageTask collageTask;
    private ArrayList<String> files;

    public Builder() {
      this.collageTask = new CollageTask();
      files = new ArrayList<>();
    }

    public Builder addFile(String handle) {
      files.add(handle);
      return this;
    }

    public Builder margin(int margin) {
      collageTask.addOption("margin", margin);
      return this;
    }

    public Builder width(int width) {
      collageTask.addOption("width", width);
      return this;
    }

    public Builder height(int height) {
      collageTask.addOption("height", height);
      return this;
    }

    public Builder color(String color) {
      collageTask.addOption("color", color);
      return this;
    }

    public Builder fit(String fit) {
      collageTask.addOption("fit", fit);
      return this;
    }

    public Builder autoRotate(boolean autoRotate) {
      collageTask.addOption("autorotate", autoRotate);
      return this;
    }

    public CollageTask build() {
      collageTask.addOption("files", files);
      return collageTask;
    }
  }
}
