package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.filestack.transforms.tasks.ShadowTask;
import org.junit.Test;

public class TestShadowTask {

  @Test
  public void testToString() {
    String correct = "shadow="
        + "blur:10,"
        + "opacity:35,"
        + "vector:[25,25],"
        + "color:white,"
        + "background:black";

    TransformTask task = new ShadowTask.Builder()
        .blur(10)
        .opacity(35)
        .vector(25, 25)
        .color("white")
        .background("black")
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
