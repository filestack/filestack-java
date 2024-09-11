package org.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import org.filestack.transforms.TransformTask;
import org.junit.Test;

public class TestAsciiTask {

  @Test
  public void testToString() {
    String correct = "ascii="
        + "background:black,"
        + "foreground:white,"
        + "colored:true,"
        + "size:640,"
        + "reverse:true";

    TransformTask task = new AsciiTask.Builder()
        .background("black")
        .foreground("white")
        .colored(true)
        .size(640)
        .reverse(true)
        .build();

    String output = task.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
