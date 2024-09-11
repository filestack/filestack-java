package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.OilPaintTask;
import org.junit.Test;

public class TestOilPaintTask {

  @Test
  public void testToString() {
    String correct = "oil_paint";

    TransformTask task = new OilPaintTask();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testToStringAmount() {
    String correct = "oil_paint="
        + "amount:5";

    TransformTask oilPaintTask = new OilPaintTask(5);

    String output = oilPaintTask.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
