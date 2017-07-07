package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestBorderTask {

    @Test
    public void testToString() {
        String correct = "border=width:5,color:white,background:black";

        TransformTask borderTask = new BorderTask.Builder()
                .width(5)
                .color("white")
                .background("black")
                .build();

        String output = borderTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
