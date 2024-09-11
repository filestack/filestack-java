package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class BlackWhiteTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public BlackWhiteTask() {
    super("blackwhite");
  }

  // Builder doesn't make sense for this task, there's only 1 option
  public BlackWhiteTask(int threshold) {
    super("blackwhite");
    addOption("threshold", threshold);
  }
}
