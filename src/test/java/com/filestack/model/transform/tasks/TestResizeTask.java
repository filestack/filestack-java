package com.filestack.model.transform.tasks;

import com.filestack.model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestResizeTask {

    @Test
    public void testToString() {
        String correct = "resize="
                + "width:100,"
                + "height:100,"
                + "fit:clip,"
                + "align:center";

        TransformTask task = new ResizeTask.Builder()
                .width(100)
                .height(100)
                .fit("clip")
                .align("center")
                .build();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringAlignPair() {
        String correct = "resize="
                + "width:100,"
                + "height:100,"
                + "fit:clip,"
                + "align:[top,left]";

        TransformTask task = new ResizeTask.Builder()
                .width(100)
                .height(100)
                .fit("clip")
                .align("top", "left")
                .build();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
