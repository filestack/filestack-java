package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestPixelateTask {

    @Test
    public void testToString() {
        String correct = "pixelate=amount:5";

        TransformTask pixelateTask = new PixelateTask.Builder()
                .amount(5)
                .build();

        String output = pixelateTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
