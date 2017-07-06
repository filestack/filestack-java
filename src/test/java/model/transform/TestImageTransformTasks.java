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
}
