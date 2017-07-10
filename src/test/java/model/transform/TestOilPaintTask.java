package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestOilPaintTask {

    @Test
    public void testToString() {
        String correct = "oil_paint=amount:5";

        TransformTask oilPaintTask = new OilPaintTask.Builder()
                .amount(5)
                .build();

        String output = oilPaintTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
