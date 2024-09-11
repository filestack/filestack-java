package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.junit.Test;

public class TestNoCacheOption {

  @Test
  public void testToString() {
    String correct = "cache=false";

    TransformTask task = new NoCacheOption();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
