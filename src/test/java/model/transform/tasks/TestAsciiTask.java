package model.transform.tasks;

import model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestAsciiTask {

    @Test
    public void testToString() {
        String correct = "ascii="
                + "background:black,"
                + "foreground:white,"
                + "colored:true,"
                + "size:640,"
                + "reverse:true";

        TransformTask task = new AsciiTask.Builder()
                .background("black")
                .foreground("white")
                .colored(true)
                .size(640)
                .reverse(true)
                .build();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
