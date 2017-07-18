package model.transform.tasks.filters;

import model.transform.base.TransformTask;
import model.transform.tasks.filters.BlackWhiteTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestBlackWhiteTask {

    @Test
    public void testToString() {
        String correct = "blackwhite";

        TransformTask task = new BlackWhiteTask();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringThreshold() {
        String correct = "blackwhite="
                + "threshold:50";

        TransformTask task = new BlackWhiteTask(50);

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
