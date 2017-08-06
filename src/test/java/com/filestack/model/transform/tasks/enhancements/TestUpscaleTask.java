package com.filestack.model.transform.tasks.enhancements;

import com.filestack.model.transform.base.TransformTask;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestUpscaleTask {

    @Test
    public void testToString() {
        String correct = "upscale="
                + "upscale:false,"
                + "noise:none,"
                + "style:artwork";

        TransformTask task = new UpscaleTask.Builder()
                .upscale(false)
                .noise("none")
                .style("artwork")
                .build();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
