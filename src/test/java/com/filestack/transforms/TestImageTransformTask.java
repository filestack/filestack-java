package org.filestack.transforms;

import org.junit.Assert;
import org.junit.Test;

public class TestImageTransformTask {

  @Test
  public void test() {
    ImageTransformTask transformTask = new ImageTransformTask("task_name");

    Assert.assertEquals("task_name", transformTask.toString());
  }
}
