package model.transform.tasks;

import model.transform.base.TransformTask;
import model.transform.tasks.enhancements.UpscaleTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
