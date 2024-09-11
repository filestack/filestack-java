package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class BlurTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public BlurTask() {
    super("blur");
  }

  // Builder doesn't make sense for this task, there's only 1 option
  public BlurTask(int amount) {
    super("blur");
    addOption("amount", amount);
  }
}
