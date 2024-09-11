package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class CropTask extends ImageTransformTask {

  // Constructor left public because this task must be created with all these options
  // Builder doesn't make sense for this task
  public CropTask(int x, int y, int width, int height) {
    super("crop");
    addOption("dim", new Integer[] {x, y, width, height});
  }
}
