package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestCropTask {

    @Test
    public void testToString() {
        String correct = "crop=dim:[0,0,100,100]";

        TransformTask cropTask = new CropTask.Builder()
                .x(0)
                .y(0)
                .width(100)
                .height(100)
                .build();

        String output = cropTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
