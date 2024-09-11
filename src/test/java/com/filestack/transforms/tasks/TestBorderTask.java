package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.BorderTask;
import org.junit.Test;

public class TestBorderTask {

  @Test
  public void testToString() {
    String correct = "border="
        + "width:5,"
        + "color:white,"
        + "background:black";

    TransformTask task = new BorderTask.Builder()
        .width(5)
        .color("white")
        .background("black")
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
