package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class ModulateTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public ModulateTask() {
    super("modulate");
  }

  public static class Builder {
    private ModulateTask modulateTask;

    public Builder() {
      this.modulateTask = new ModulateTask();
    }

    public Builder brightness(int brightness) {
      modulateTask.addOption("brightness", brightness);
      return this;
    }

    public Builder hue(int hue) {
      modulateTask.addOption("hue", hue);
      return this;
    }

    public Builder saturation(int saturation) {
      modulateTask.addOption("saturation", saturation);
      return this;
    }

    public ModulateTask build() {
      return modulateTask;
    }
  }
}
