package model.transform.tasks.filters;

import model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestMonochromeTask {

    @Test
    public void testToString() {
        String correct = "monochrome";

        TransformTask task = new MonochromeTask();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
