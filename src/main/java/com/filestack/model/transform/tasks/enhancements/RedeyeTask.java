package com.filestack.model.transform.tasks.enhancements;

import com.filestack.model.transform.base.ImageTransformTask;

public class RedeyeTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  // Builder doesn't make sense for this task
  public RedeyeTask() {
    super("redeye");
  }
}
