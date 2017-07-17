package model.transform.tasks.effects;

import model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestShadowTask {

    @Test
    public void testToString() {
        String correct = "shadow="
                + "blur:10,"
                + "opacity:35,"
                + "vector:[25,25],"
                + "color:white,"
                + "background:black";

        TransformTask task = new ShadowTask.Builder()
                .blur(10)
                .opacity(35)
                .vector(25, 25)
                .color("white")
                .background("black")
                .build();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
