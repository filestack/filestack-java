package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.BlurTask;
import org.junit.Test;

public class TestBlurTask {

  @Test
  public void testToString() {
    String correct = "blur";

    TransformTask task = new BlurTask();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testToStringAmount() {
    String correct = "blur="
        + "amount:5";

    TransformTask task = new BlurTask(5);

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
