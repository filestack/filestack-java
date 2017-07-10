package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestBlackWhiteTask {

    @Test
    public void testToString() {
        String correct = "blackwhite=threshold:50";

        TransformTask blackWhiteTask = new BlackWhiteTask.Builder()
                .threshold(50)
                .build();

        String output = blackWhiteTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
