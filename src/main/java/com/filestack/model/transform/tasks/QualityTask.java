package com.filestack.model.transform.tasks;

import com.filestack.model.transform.base.ImageTransformTask;

public class QualityTask extends ImageTransformTask {

  // Constructor left public because this task must be created with all these options
  // Builder doesn't make sense for this task
  public QualityTask(int value) {
    super("quality");
    addOption("value", value);
  }
}
