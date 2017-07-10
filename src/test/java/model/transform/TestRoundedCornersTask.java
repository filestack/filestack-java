package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestRoundedCornersTask {

    @Test
    public void testToString() {
        String correct = "rounded_corners=radius:100,blur:5.0,background:white";

        TransformTask roundedCornersTask = new RoundedCornersTask.Builder()
                .radius(100)
                .blur(5)
                .background("white")
                .build();

        String output = roundedCornersTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringRadiusMax() {
        String correct = "rounded_corners=radius:max";

        TransformTask roundedCornersTask = new RoundedCornersTask.Builder()
                .radius("max")
                .build();

        String output = roundedCornersTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
