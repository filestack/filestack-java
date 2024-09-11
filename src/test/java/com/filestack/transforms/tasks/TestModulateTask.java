package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.ModulateTask;
import org.junit.Test;

public class TestModulateTask {

  @Test
  public void testToString() {
    String correct = "modulate="
        + "brightness:155,"
        + "hue:155,"
        + "saturation:155";

    TransformTask task = new ModulateTask.Builder()
        .brightness(155)
        .hue(155)
        .saturation(155)
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
