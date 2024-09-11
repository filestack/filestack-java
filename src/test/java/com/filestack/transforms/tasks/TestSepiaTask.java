package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.SepiaTask;
import org.junit.Test;

public class TestSepiaTask {

  @Test
  public void testToString() {
    String correct = "sepia";

    TransformTask task = new SepiaTask();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testToStringTone() {
    String correct = "sepia="
        + "tone:80";

    TransformTask task = new SepiaTask(80);

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
