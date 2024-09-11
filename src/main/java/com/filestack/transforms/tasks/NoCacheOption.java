package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class NoCacheOption extends ImageTransformTask {

  // Builder doesn't make sense for this task
  public NoCacheOption() {
    super("cache=false");
  }
}
