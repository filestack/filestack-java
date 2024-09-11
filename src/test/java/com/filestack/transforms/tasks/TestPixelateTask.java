package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.PixelateTask;
import org.junit.Test;

public class TestPixelateTask {

  @Test
  public void testToString() {
    String correct = "pixelate";

    TransformTask task = new PixelateTask();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testToStringAmount() {
    String correct = "pixelate="
        + "amount:5";

    TransformTask task = new PixelateTask(5);

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
