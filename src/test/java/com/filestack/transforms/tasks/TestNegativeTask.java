package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.NegativeTask;
import org.junit.Test;

public class TestNegativeTask {

  @Test
  public void testToString() {
    String correct = "negative";

    TransformTask task = new NegativeTask();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
