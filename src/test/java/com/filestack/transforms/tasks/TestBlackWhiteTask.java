package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.BlackWhiteTask;
import org.junit.Test;

public class TestBlackWhiteTask {

  @Test
  public void testToString() {
    String correct = "blackwhite";

    TransformTask task = new BlackWhiteTask();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testToStringThreshold() {
    String correct = "blackwhite="
        + "threshold:50";

    TransformTask task = new BlackWhiteTask(50);

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
