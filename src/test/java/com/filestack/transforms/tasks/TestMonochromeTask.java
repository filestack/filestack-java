package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.MonochromeTask;
import org.junit.Test;

public class TestMonochromeTask {

  @Test
  public void testToString() {
    String correct = "monochrome";

    TransformTask task = new MonochromeTask();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
