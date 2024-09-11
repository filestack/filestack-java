package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.PolaroidTask;
import org.junit.Test;

public class TestPolaroidTask {

  @Test
  public void testToString() {
    String correct = "polaroid="
        + "color:white,"
        + "rotate:90,"
        + "background:black";

    TransformTask task = new PolaroidTask.Builder()
        .color("white")
        .rotate(90)
        .background("black")
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
