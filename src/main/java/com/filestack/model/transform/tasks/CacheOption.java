package com.filestack.model.transform.tasks;

import com.filestack.model.transform.base.ImageTransformTask;

public class CacheOption extends ImageTransformTask {

  // Builder doesn't make sense for this task
  public CacheOption(int expiry) {
    super("cache");
    addOption("expiry", expiry);
  }
}
