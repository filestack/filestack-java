package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestWatermarkTask {

    @Test
    public void testToString() {
        String correct = "watermark=file:VsXhJVWReSrYU7UoFyfw,size:75,position:top";

        TransformTask watermarkTask = new WatermarkTask.Builder()
                .file("VsXhJVWReSrYU7UoFyfw")
                .size(75)
                .position("top")
                .build();

        String output = watermarkTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringPositionPair() {
        String correct = "watermark=file:VsXhJVWReSrYU7UoFyfw,size:75,position:[top,right]";

        TransformTask watermarkTask = new WatermarkTask.Builder()
                .file("VsXhJVWReSrYU7UoFyfw")
                .size(75)
                .position("top", "right")
                .build();

        String output = watermarkTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
