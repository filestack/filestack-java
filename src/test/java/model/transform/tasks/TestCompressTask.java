package model.transform.tasks;

import model.transform.base.TransformTask;
import model.transform.tasks.effects.CircleTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestCompressTask {

    @Test
    public void testToString() {
        String correct = "compress";

        TransformTask task = new CompressTask();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringBackground() {
        String correct = "compress="
                + "metadata:true";

        TransformTask task = new CompressTask(true);

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
