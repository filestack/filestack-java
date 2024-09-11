package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.CircleTask;
import org.junit.Test;

public class TestCircleTask {

  @Test
  public void testToString() {
    String correct = "circle";

    TransformTask task = new CircleTask();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testToStringBackground() {
    String correct = "circle="
        + "background:white";

    TransformTask task = new CircleTask("white");

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
