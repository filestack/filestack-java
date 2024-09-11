package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.CropFacesTask;
import org.junit.Test;

public class TestCropFacesTask {

  @Test
  public void testToString() {
    String correct = "crop_faces="
        + "mode:thumb,"
        + "width:100,"
        + "height:100,"
        + "faces:1,"
        + "buffer:200";

    TransformTask task = new CropFacesTask.Builder()
        .mode("thumb")
        .width(100)
        .height(100)
        .faces(1)
        .buffer(200)
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testToStringFacesArray() {
    String correct = "crop_faces="
        + "mode:thumb,"
        + "width:100,"
        + "height:100,"
        + "faces:[1,2,3,4],"
        + "buffer:200";

    TransformTask task = new CropFacesTask.Builder()
        .mode("thumb")
        .width(100)
        .height(100)
        .faces(1, 2, 3, 4)
        .buffer(200)
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testToStringFacesAll() {
    String correct = "crop_faces="
        + "mode:thumb,"
        + "width:100,"
        + "height:100,"
        + "faces:all,"
        + "buffer:200";

    TransformTask task = new CropFacesTask.Builder()
        .mode("thumb")
        .width(100)
        .height(100)
        .faces("all")
        .buffer(200)
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
