package model.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestImageTransformTasks {

    @Test
    public void testStoreOptionsToString() {
        String correct = "store=filename:some_file.txt,location:S3,path:/some/path/,container:some_bucket,"
                + "region:us-east-1,access:private,base64decode:false";

        TransformTask storeOptions = new StoreOptions.Builder()
                .filename("some_file.txt")
                .location("S3")
                .path("/some/path/")
                .container("some_bucket")
                .region("us-east-1")
                .access("private")
                .base64Decode(false)
                .build();

        String output = storeOptions.toString();

        String message = String.format("Store task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testResizeTaskToString() {
        String correct = "resize=width:100,height:100,fit:clip,align:center";

        TransformTask resizeTask = new ResizeTask.Builder()
                .width(100)
                .height(100)
                .fit("clip")
                .align("center")
                .build();

        String output = resizeTask.toString();

        String message = String.format("Resize task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testCropTaskToString() {
        String correct = "crop=dim:[0,0,100,100]";

        TransformTask cropTask = new CropTask(0,0,100,100);

        String output = cropTask.toString();

        String message = String.format("Resize task string malformed\nCorrect: %s\nOutput: %s", correct, output);
        assertTrue(message, output.equals(correct));
    }
}
