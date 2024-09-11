package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.PartialBlurTask;
import org.junit.Test;

public class TestPartialBlurTask {

  @Test
  public void testToString() {
    String correct = "partial_pixelate="
        + "amount:10.0,"
        + "blur:10.0,"
        + "type:rect,"
        + "objects:[[10,20,200,250]]";

    TransformTask task = new PartialBlurTask.Builder()
        .amount(10)
        .blur(10)
        .type("rect")
        .addArea(10, 20, 200, 250)
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testToStringMultipleAreas() {
    String correct = "partial_pixelate="
        + "amount:10.0,"
        + "blur:10.0,"
        + "type:rect,"
        + "objects:[[10,20,200,250],[275,91,500,557]]";

    TransformTask task = new PartialBlurTask.Builder()
        .amount(10)
        .blur(10)
        .type("rect")
        .addArea(10, 20, 200, 250)
        .addArea(275, 91, 500, 557)
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
