package model.transform.tasks.effects;

import model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestRoundedCornersTask {

    @Test
    public void testToString() {
        String correct = "rounded_corners=radius:100,blur:5.0,background:white";

        TransformTask task = new RoundedCornersTask.Builder()
                .radius(100)
                .blur(5)
                .background("white")
                .build();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringRadiusMax() {
        String correct = "rounded_corners=radius:max";

        TransformTask task = new RoundedCornersTask.Builder()
                .radius("max")
                .build();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
