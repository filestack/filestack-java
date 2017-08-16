package com.filestack.transforms.tasks;

import com.filestack.transforms.ImageTransformTask;

public class CacheOption extends ImageTransformTask {

  // Builder doesn't make sense for this task
  public CacheOption(int expiry) {
    super("cache");
    addOption("expiry", expiry);
  }
}
