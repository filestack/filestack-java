package model.transform.tasks.filters;

import model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestSepiaTask {

    @Test
    public void testToString() {
        String correct = "sepia";

        TransformTask task = new SepiaTask();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringTone() {
        String correct = "sepia="
                + "tone:80";

        TransformTask task = new SepiaTask(80);

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
