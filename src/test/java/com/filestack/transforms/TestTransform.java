package com.filestack.transforms;

import static com.filestack.util.MockConstants.API_KEY;
import static com.filestack.util.MockConstants.FILE_LINK;
import static com.filestack.util.MockConstants.FILE_LINK_SECURITY;
import static com.filestack.util.MockConstants.FS_CLIENT;
import static com.filestack.util.MockConstants.HANDLE;
import static com.filestack.util.MockConstants.SECURITY;
import static org.junit.Assert.assertTrue;

import com.filestack.transforms.Transform;
import com.filestack.transforms.TransformTask;
import com.filestack.util.FilestackService;
import org.junit.Test;

public class TestTransform {
  private static final TransformTask TASK = new TransformTask("task");

  static {
    TASK.addOption("option1", 1);
    TASK.addOption("option2", 1.0);
    TASK.addOption("option3", "value");
    TASK.addOption("option4", new Integer[] {1, 1, 1, 1});
  }

  private static final String TASK_STRING
      = "task=option1:1,option2:1.0,option3:value,option4:[1,1,1,1]";
  private static final String SOURCE = "https://example.com/image.jpg";

  @Test
  public void testUrl() {
    String correct = FilestackService.Process.URL + TASK_STRING + "/" + HANDLE;
    Transform transform = new Transform(FILE_LINK);
    transform.tasks.add(TASK);
    String output = transform.url();

    String message = String.format("Bad transform URL (basic)\nCorrect: %s\nOutput:  %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testUrlSecurity() {
    String correct = FilestackService.Process.URL
        + "security=policy:" + SECURITY.getPolicy() + ","
        + "signature:" + SECURITY.getSignature() + "/"
        + TASK_STRING + "/" + HANDLE;

    Transform transform = new Transform(FILE_LINK_SECURITY);
    transform.tasks.add(TASK);
    String output = transform.url();

    String message = String.format("Bad transform URL (security)\nCorrect: %s\nOutput:  %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testUrlExternal() {
    String correct = FilestackService.Process.URL + API_KEY + "/" + TASK_STRING + "/" + SOURCE;
    Transform transform = new Transform(FS_CLIENT, SOURCE);
    transform.tasks.add(TASK);
    String output = transform.url();

    String message = String.format("Bad transform URL (external)\nCorrect: %s\nOutput:  %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testUrlMultipleTasks() {
    String correct = FilestackService.Process.URL + TASK_STRING + "/" + TASK_STRING
        + "/" + HANDLE;

    Transform transform = new Transform(FILE_LINK);
    transform.tasks.add(TASK);
    transform.tasks.add(TASK);
    String output = transform.url();

    String message = String.format("Bad transform URL (multiple)\nCorrect: %s\nOutput:  %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testUrlTaskWithoutOptions() {
    String correct = FilestackService.Process.URL + "task/" + HANDLE;

    Transform transform = new Transform(FILE_LINK);
    transform.tasks.add(new TransformTask("task"));
    String output = transform.url();

    String message = String.format("Bad transform URL (no options)\nCorrect: %s\nOutput:  %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }
}
