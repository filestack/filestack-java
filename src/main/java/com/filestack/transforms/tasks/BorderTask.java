package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class BorderTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public BorderTask() {
    super("border");
  }

  public static class Builder {
    private BorderTask borderTask;

    public Builder() {
      this.borderTask = new BorderTask();
    }

    public Builder width(int width) {
      borderTask.addOption("width", width);
      return this;
    }

    public Builder color(String color) {
      borderTask.addOption("color", color);
      return this;
    }

    public Builder background(String background) {
      borderTask.addOption("background", background);
      return this;
    }

    public BorderTask build() {
      return borderTask;
    }
  }
}
