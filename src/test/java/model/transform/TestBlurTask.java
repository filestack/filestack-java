package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestBlurTask {

    @Test
    public void testToString() {
        String correct = "blur=amount:5";

        TransformTask blurTask = new BlurTask.Builder()
                .amount(5)
                .build();

        String output = blurTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
