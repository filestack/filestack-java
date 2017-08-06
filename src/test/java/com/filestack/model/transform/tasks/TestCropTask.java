package com.filestack.model.transform.tasks;

import com.filestack.model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestCropTask {

    @Test
    public void testToString() {
        String correct = "crop="
                + "dim:[0,0,100,100]";

        TransformTask task = new CropTask(0, 0, 100, 100);

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}