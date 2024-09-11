package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class CircleTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public CircleTask() {
    super("circle");
  }

  // Builder doesn't make sense for this task, there's only 1 option
  public CircleTask(String background) {
    super("circle");
    addOption("background", background);
  }
}
