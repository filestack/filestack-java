package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestPolaroidTask {

    @Test
    public void testToString() {
        String correct = "polaroid=color:white,rotate:90,background:black";

        TransformTask polaroidTask = new PolaroidTask.Builder()
                .color("white")
                .rotate(90)
                .background("black")
                .build();

        String output = polaroidTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
