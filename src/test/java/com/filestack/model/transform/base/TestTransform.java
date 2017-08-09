package com.filestack.model.transform.base;

import org.junit.Test;
import com.filestack.util.FilestackService;

import static org.junit.Assert.assertTrue;
import static com.filestack.util.MockConstants.*;

public class TestTransform {
    private static final TransformTask TASK = new TransformTask("task");
    static {
        TASK.addOption("option1", 1);
        TASK.addOption("option2", 1.0);
        TASK.addOption("option3", "value");
        TASK.addOption("option4", new Integer[]{1,1,1,1});
    }
    private static final String TASK_STRING = "task=option1:1,option2:1.0,option3:value,option4:[1,1,1,1]";
    private static final String SOURCE = "https://example.com/image.jpg";

    @Test
    public void testUrl() {
        String correct = FilestackService.Process.URL + TASK_STRING + "/" + HANDLE;
        Transform transform = new Transform(FILE_LINK);
        transform.tasks.add(TASK);
        String output = transform.url();

        String message = String.format("Bad transform URL (basic)\nCorrect: %s\nOutput:  %s", correct, output);
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

        String message = String.format("Bad transform URL (security)\nCorrect: %s\nOutput:  %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
    
    @Test
    public void testUrlExternal() {
        String correct = FilestackService.Process.URL + API_KEY + "/" + TASK_STRING + "/" + SOURCE;
        Transform transform = new Transform(FS_CLIENT, SOURCE);
        transform.tasks.add(TASK);
        String output = transform.url();

        String message = String.format("Bad transform URL (external)\nCorrect: %s\nOutput:  %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testUrlMultipleTasks() {
        String correct = FilestackService.Process.URL + TASK_STRING + "/" + TASK_STRING + "/" + HANDLE;

        Transform transform = new Transform(FILE_LINK);
        transform.tasks.add(TASK);
        transform.tasks.add(TASK);
        String output = transform.url();

        String message = String.format("Bad transform URL (multiple tasks)\nCorrect: %s\nOutput:  %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testUrlTaskWithoutOptions() {
        String correct = FilestackService.Process.URL + "task/" + HANDLE;

        Transform transform = new Transform(FILE_LINK);
        transform.tasks.add(new TransformTask("task"));
        String output = transform.url();

        String message = String.format("Bad transform URL (no options)\nCorrect: %s\nOutput:  %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
