package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.junit.Test;

public class TestUrlScreenshotTask {

  @Test
  public void testToString() {
    String correct = "urlscreenshot="
        + "agent:desktop,"
        + "mode:all,"
        + "width:1920,"
        + "height:1080,"
        + "delay:3000";

    TransformTask task = new UrlScreenshotTask.Builder()
        .agent("desktop")
        .mode("all")
        .width(1920)
        .height(1080)
        .delay(3000)
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
