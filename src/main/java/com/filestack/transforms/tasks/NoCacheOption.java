package com.filestack.transforms.tasks;

import com.filestack.transforms.ImageTransformTask;

public class NoCacheOption extends ImageTransformTask {

  // Builder doesn't make sense for this task
  public NoCacheOption() {
    super("cache=false");
  }
}
