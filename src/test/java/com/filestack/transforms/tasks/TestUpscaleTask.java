package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.UpscaleTask;
import org.junit.Test;

public class TestUpscaleTask {

  @Test
  public void testToString() {
    String correct = "upscale="
        + "upscale:false,"
        + "noise:none,"
        + "style:artwork";

    TransformTask task = new UpscaleTask.Builder()
        .upscale(false)
        .noise("none")
        .style("artwork")
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
