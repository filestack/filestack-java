package org.filestack.transforms.tasks;

import org.filestack.transforms.TransformTask;
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
