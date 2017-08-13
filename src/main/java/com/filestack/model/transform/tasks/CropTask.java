package com.filestack.model.transform.tasks;

import com.filestack.model.transform.base.ImageTransformTask;

public class CropTask extends ImageTransformTask {

  // Constructor left public because this task must be created with all these options
  // Builder doesn't make sense for this task
  public CropTask(int x, int y, int width, int height) {
    super("crop");
    addOption("dim", new Integer[] {x, y, width, height});
  }
}
