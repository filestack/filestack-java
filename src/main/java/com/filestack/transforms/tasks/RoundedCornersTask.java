package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class RoundedCornersTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public RoundedCornersTask() {
    super("rounded_corners");
  }

  public static class Builder {
    private RoundedCornersTask roundedCornersTask;

    public Builder() {
      this.roundedCornersTask = new RoundedCornersTask();
    }

    public Builder radius(int radius) {
      roundedCornersTask.addOption("radius", radius);
      return this;
    }

    // For "max" value
    public Builder radius(String radius) {
      roundedCornersTask.addOption("radius", radius);
      return this;
    }

    public Builder blur(double blur) {
      roundedCornersTask.addOption("blur", blur);
      return this;
    }

    public Builder background(String background) {
      roundedCornersTask.addOption("background", background);
      return this;
    }

    public RoundedCornersTask build() {
      return roundedCornersTask;
    }
  }
}
