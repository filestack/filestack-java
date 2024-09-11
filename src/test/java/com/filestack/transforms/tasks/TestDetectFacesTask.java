package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.DetectFacesTask;
import org.junit.Test;

public class TestDetectFacesTask {

  @Test
  public void testToStringInts() {
    String correct = "detect_faces="
        + "minsize:100,"
        + "maxsize:100,"
        + "color:white,"
        + "export:false";

    TransformTask task = new DetectFacesTask.Builder()
        .minSize(100)
        .maxSize(100)
        .color("white")
        .export(false)
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testToStringFloats() {
    String correct = "detect_faces="
        + "minsize:0.25,"
        + "maxsize:0.75,"
        + "color:white,"
        + "export:false";

    TransformTask task = new DetectFacesTask.Builder()
        .minSize(.25)
        .maxSize(.75)
        .color("white")
        .export(false)
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
