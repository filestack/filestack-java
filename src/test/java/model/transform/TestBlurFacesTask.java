package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestBlurFacesTask {

    @Test
    public void testToString() {
        String correct = "blur_faces=faces:1,minsize:200,maxsize:300,buffer:50,amount:5.0,blur:10.0,type:oval";

        TransformTask blurFacesTask = new BlurFacesTask.Builder()
                .faces(1)
                .minSize(200)
                .maxSize(300)
                .buffer(50)
                .amount(5.0)
                .blur(10)
                .type("oval")
                .build();

        String output = blurFacesTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringFacesArray() {
        String correct = "blur_faces=faces:[1,2,3,4]";

        TransformTask blurFacesTask = new BlurFacesTask.Builder()
                .faces(1, 2, 3, 4)
                .build();

        String output = blurFacesTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringFacesAll() {
        String correct = "blur_faces=faces:all";

        TransformTask blurFacesTask = new BlurFacesTask.Builder()
                .faces("all")
                .build();

        String output = blurFacesTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testToStringSizeFloats() {
        String correct = "blur_faces=minsize:0.35,maxsize:0.35";

        TransformTask blurFacesTask = new BlurFacesTask.Builder()
                .minSize(.35)
                .maxSize(.35)
                .build();

        String output = blurFacesTask.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
