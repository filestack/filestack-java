package model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestTransform {

    @Test
    public void testTaskToString() {
        String correct = "task=option1:1,option2:1.0,option3:value,option4:[1,1,1,1],";

        Transform.Task task = new Transform.Task("task");
        task.addOption("option1", 1);
        task.addOption("option2", 1.0);
        task.addOption("option3", "value");
        task.addOption("option4", new Integer[]{1,1,1,1});
        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
