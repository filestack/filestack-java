package com.filestack.model.transform.tasks.enhancements;

import com.filestack.model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestEnhanceTask {

    @Test
    public void testToString() {
        String correct = "enhance";

        TransformTask task = new EnhanceTask();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
