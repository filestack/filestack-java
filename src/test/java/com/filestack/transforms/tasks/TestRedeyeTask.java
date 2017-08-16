package com.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import com.filestack.transforms.TransformTask;
import com.filestack.transforms.tasks.RedeyeTask;
import org.junit.Test;

public class TestRedeyeTask {

  @Test
  public void testToString() {
    String correct = "redeye";

    TransformTask task = new RedeyeTask();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
