package com.filestack.transforms.tasks;

import com.filestack.transforms.TransformTask;
import org.junit.Assert;
import org.junit.Test;

public class TestSfwTask {

  @Test
  public void testToString() {
    String correct = "sfw";

    TransformTask task = new SfwTask();

    Assert.assertEquals(correct, task.toString());
  }
}
