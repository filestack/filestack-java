package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestSepiaTask {

    @Test
    public void testToString() {
        String correct = "sepia=tone:80";

        TransformTask sepiaTask = new SepiaTask.Builder()
                .tone(80)
                .build();

        String output = sepiaTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
