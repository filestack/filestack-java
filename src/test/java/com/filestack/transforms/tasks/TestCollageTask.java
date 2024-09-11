package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.junit.Test;

public class TestCollageTask {

  @Test
  public void testToString() {
    String correct = "collage="
        + "margin:50,"
        + "width:1000,"
        + "height:1000,"
        + "color:white,"
        + "fit:crop,"
        + "autorotate:true,"
        + "files:[0ZgN5BtJTfmI1O3Rxhce,6a9QVg1LS4uoPN7B4HYA]";

    TransformTask task = new CollageTask.Builder()
        .margin(50)
        .width(1000)
        .height(1000)
        .color("white")
        .fit("crop")
        .autoRotate(true)
        .addFile("0ZgN5BtJTfmI1O3Rxhce")
        .addFile("6a9QVg1LS4uoPN7B4HYA")
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
