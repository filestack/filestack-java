package com.filestack.transforms.tasks;

import com.filestack.transforms.TransformTask;
import org.junit.Assert;
import org.junit.Test;

public class TestTaggingTask {

  @Test
  public void testToString() {
    String correct = "tags";

    TransformTask task = new TaggingTask();

    Assert.assertEquals(correct, task.toString());
  }
}
