package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class OilPaintTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public OilPaintTask() {
    super("oil_paint");
  }

  // Builder doesn't make sense for this task, there's only 1 option
  public OilPaintTask(int amount) {
    super("oil_paint");
    addOption("amount", amount);
  }
}
