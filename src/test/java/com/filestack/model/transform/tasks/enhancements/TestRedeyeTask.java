package com.filestack.model.transform.tasks.enhancements;

import com.filestack.model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestRedeyeTask {

    @Test
    public void testToString() {
        String correct = "redeye";

        TransformTask task = new RedeyeTask();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
