package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class SharpenTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public SharpenTask() {
    super("sharpen");
  }

  // Builder doesn't make sense for this task, there's only 1 option
  public SharpenTask(int amount) {
    super("sharpen");
    addOption("amount", amount);
  }
}
