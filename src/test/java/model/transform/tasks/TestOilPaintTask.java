package model.transform.tasks;

import model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestOilPaintTask {

    @Test
    public void testToString() {
        String correct = "oil_paint";

        TransformTask task = new OilPaintTask();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringAmount() {
        String correct = "oil_paint="
                + "amount:5";

        TransformTask oilPaintTask = new OilPaintTask(5);

        String output = oilPaintTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
