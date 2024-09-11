package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.junit.Test;

public class TestCropTask {

  @Test
  public void testToString() {
    String correct = "crop="
        + "dim:[0,0,100,100]";

    TransformTask task = new CropTask(0, 0, 100, 100);

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
