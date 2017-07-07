package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestFlopTask {

    @Test
    public void testToString() {
        String correct = "flop";

        TransformTask flopTask = new FlopTask();

        String output = flopTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
