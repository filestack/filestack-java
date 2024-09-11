package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class CompressTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public CompressTask() {
    super("compress");
  }

  // Builder doesn't make sense for this task, there's only 1 option
  public CompressTask(boolean metadata) {
    super("compress");
    addOption("metadata", metadata);
  }
}
