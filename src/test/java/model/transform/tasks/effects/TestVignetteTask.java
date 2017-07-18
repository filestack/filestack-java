package model.transform.tasks.effects;

import model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestVignetteTask {

    @Test
    public void testToString() {
        String correct = "vignette="
                + "amount:50,"
                + "blurmode:linear,"
                + "background:white";

        TransformTask task = new VignetteTask.Builder()
                .amount(50)
                .blurMode("linear")
                .background("white")
                .build();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
