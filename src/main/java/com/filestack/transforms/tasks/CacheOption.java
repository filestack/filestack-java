package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class CacheOption extends ImageTransformTask {

  // Builder doesn't make sense for this task
  public CacheOption(int expiry) {
    super("cache");
    addOption("expiry", expiry);
  }
}
