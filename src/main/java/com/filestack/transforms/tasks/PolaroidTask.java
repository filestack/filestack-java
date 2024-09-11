package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class PolaroidTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public PolaroidTask() {
    super("polaroid");
  }

  public static class Builder {
    private PolaroidTask polaroidTask;

    public Builder() {
      this.polaroidTask = new PolaroidTask();
    }

    public Builder color(String color) {
      polaroidTask.addOption("color", color);
      return this;
    }

    public Builder rotate(int rotate) {
      polaroidTask.addOption("rotate", rotate);
      return this;
    }

    public Builder background(String background) {
      polaroidTask.addOption("background", background);
      return this;
    }

    public PolaroidTask build() {
      return polaroidTask;
    }
  }
}
