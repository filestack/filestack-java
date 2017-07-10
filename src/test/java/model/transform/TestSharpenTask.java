package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestSharpenTask {

    @Test
    public void testToString() {
        String correct = "sharpen=amount:5";

        TransformTask sharpenTask = new SharpenTask.Builder()
                .amount(5)
                .build();

        String output = sharpenTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
