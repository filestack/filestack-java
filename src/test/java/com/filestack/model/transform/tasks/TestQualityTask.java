package com.filestack.model.transform.tasks;

import com.filestack.model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestQualityTask {

    @Test
    public void testToString() {
        String correct = "quality="
                + "value:75";

        TransformTask task = new QualityTask(75);

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}