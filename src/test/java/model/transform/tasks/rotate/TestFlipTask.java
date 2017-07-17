package model.transform.tasks.rotate;

import model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestFlipTask {

    @Test
    public void testToString() {
        String correct = "flip";

        TransformTask task = new FlipTask();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
