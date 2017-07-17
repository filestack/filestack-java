package model.transform.tasks;

import model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestSharpenTask {

    @Test
    public void testToString() {
        String correct = "sharpen";

        TransformTask task = new SharpenTask();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringAmount() {
        String correct = "sharpen="
                + "amount:5";

        TransformTask task = new SharpenTask(5);

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
