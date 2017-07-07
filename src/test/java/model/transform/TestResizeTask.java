package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestResizeTask {

    @Test
    public void testToString() {
        String correct = "resize=width:100,height:100,fit:clip,align:center";

        TransformTask resizeTask = new ResizeTask.Builder()
                .width(100)
                .height(100)
                .fit("clip")
                .align("center")
                .build();

        String output = resizeTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringAlignPair() {
        String correct = "resize=width:100,height:100,fit:clip,align:[top,left]";

        TransformTask resizeTask = new ResizeTask.Builder()
                .width(100)
                .height(100)
                .fit("clip")
                .align("top", "left")
                .build();

        String output = resizeTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
