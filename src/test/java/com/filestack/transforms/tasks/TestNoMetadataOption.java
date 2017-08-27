package com.filestack.transforms.tasks;

import com.filestack.transforms.TransformTask;
import org.junit.Assert;
import org.junit.Test;

public class TestNoMetadataOption {

  @Test
  public void testToString() {
    String correct = "no_metadata";

    TransformTask task = new NoMetadataOption();

    String output = task.toString();

    Assert.assertEquals(correct, output);
  }
}
