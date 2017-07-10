package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestCircleTask {

    @Test
    public void testToString() {
        String correct = "circle=background:white";

        TransformTask circleTask = new CircleTask.Builder()
                .background("white")
                .build();

        String output = circleTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
