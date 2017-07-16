package model.transform.tasks;

import model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestCircleTask {

    @Test
    public void testToString() {
        String correct = "circle";

        TransformTask task = new CircleTask();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringBackground() {
        String correct = "circle="
                + "background:white";

        TransformTask task = new CircleTask("white");

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
