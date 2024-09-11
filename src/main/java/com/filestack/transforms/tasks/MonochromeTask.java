package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class MonochromeTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  // Builder doesn't make sense for this task
  public MonochromeTask() {
    super("monochrome");
  }
}
