package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.VignetteTask;
import org.junit.Test;

public class TestVignetteTask {

  @Test
  public void testToString() {
    String correct = "vignette="
        + "amount:50,"
        + "blurmode:linear,"
        + "background:white";

    TransformTask task = new VignetteTask.Builder()
        .amount(50)
        .blurMode("linear")
        .background("white")
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
