package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class ShadowTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public ShadowTask() {
    super("shadow");
  }

  public static class Builder {
    private ShadowTask shadowTask;

    public Builder() {
      this.shadowTask = new ShadowTask();
    }

    public Builder blur(int blur) {
      shadowTask.addOption("blur", blur);
      return this;
    }

    public Builder opacity(int opacity) {
      shadowTask.addOption("opacity", opacity);
      return this;
    }

    public Builder vector(int x, int y) {
      shadowTask.addOption("vector", new Integer[] {x, y});
      return this;
    }

    public Builder color(String color) {
      shadowTask.addOption("color", color);
      return this;
    }

    public Builder background(String background) {
      shadowTask.addOption("background", background);
      return this;
    }

    public ShadowTask build() {
      return shadowTask;
    }
  }
}
