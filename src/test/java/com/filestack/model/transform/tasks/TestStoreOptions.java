package com.filestack.model.transform.tasks;

import static org.junit.Assert.assertTrue;

import com.filestack.model.transform.base.TransformTask;
import org.junit.Test;

public class TestStoreOptions {

    @Test
    public void testToString() {
        String correct = "store="
                + "filename:some_file.txt,"
                + "location:S3,"
                + "path:/some/path/,"
                + "container:some_bucket,"
                + "region:us-east-1,"
                + "access:private,"
                + "base64decode:false";

        TransformTask task = new StoreOptions.Builder()
                .filename("some_file.txt")
                .location("S3")
                .path("/some/path/")
                .container("some_bucket")
                .region("us-east-1")
                .access("private")
                .base64Decode(false)
                .build();

        String output = task.toString();

        String message = String.format("Task string malformed\nCorrect: %s\nOutput: %s",
                correct, output);
        assertTrue(message, output.equals(correct));
    }
}
