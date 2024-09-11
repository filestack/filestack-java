package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.SharpenTask;
import org.junit.Test;

public class TestSharpenTask {

  @Test
  public void testToString() {
    String correct = "sharpen";

    TransformTask task = new SharpenTask();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testToStringAmount() {
    String correct = "sharpen="
        + "amount:5";

    TransformTask task = new SharpenTask(5);

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
