package org.filestack.transforms;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestTransformTask {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final TransformTask TASK = new TransformTask("task");

  static {
    TASK.addOption("option1", 1);
    TASK.addOption("option2", 1.0);
    TASK.addOption("option3", "value");
    TASK.addOption("option4", new Integer[] {1, 1, 1, 1});
  }

  private static final String TASK_STRING =
      "task=option1:1,option2:1.0,option3:value,option4:[1,1,1,1]";

  @Test
  public void testTaskToString() {
    String output = TASK.toString();

    String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
        TASK_STRING, output);
    assertTrue(message, output.equals(TASK_STRING));
  }

  @Test
  public void testOptionNullKey() {
    TransformTask transformTask = new TransformTask("test");

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Task option key cannot be empty");
    transformTask.addOption(null, "");
  }

  @Test
  public void testOptionNullValue() {
    TransformTask transformTask = new TransformTask("test");
    Assert.assertEquals(0, transformTask.options.size());
    transformTask.addOption("key", null);
    Assert.assertEquals(0, transformTask.options.size());
  }
  
  @Test
  public void testMerge() {
    TransformTask first = new TransformTask("first");
    TransformTask second = new TransformTask("second");
    TransformTask third = new TransformTask("third");

    first.addOption("option1", "value1");
    second.addOption("option2", "value2");
    third.addOption("option3", "value3");

    String correct = "merged="
        + "option1:value1,"
        + "option2:value2,"
        + "option3:value3";

    TransformTask merged = TransformTask.merge("merged", first, second, third);

    Assert.assertEquals(correct, merged.toString());
  }
}
