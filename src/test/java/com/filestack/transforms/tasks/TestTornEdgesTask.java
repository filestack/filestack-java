package com.filestack.transforms.tasks;

import static org.junit.Assert.assertTrue;

import com.filestack.transforms.TransformTask;
import com.filestack.transforms.tasks.TornEdgesTask;
import org.junit.Test;

public class TestTornEdgesTask {

  @Test
  public void testToString() {
    String correct = "torn_edges="
        + "spread:[10,50],"
        + "background:white";

    TransformTask tasks = new TornEdgesTask.Builder()
        .spread(10, 50)
        .background("white")
        .build();

    String output = tasks.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
