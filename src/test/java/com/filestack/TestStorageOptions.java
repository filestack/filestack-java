package com.filestack;

import org.junit.Assert;
import org.junit.Test;

public class TestStorageOptions {

  @Test
  public void testGetTask() {
    String correct = "store="
        + "access:private,"
        + "base64decode:false,"
        + "container:some_bucket,"
        + "filename:some_file.txt,"
        + "location:S3,"
        + "path:/some/path/,"
        + "region:us-east-1";

    StorageOptions options = new StorageOptions.Builder()
        .filename("some_file.txt")
        .location("S3")
        .path("/some/path/")
        .container("some_bucket")
        .region("us-east-1")
        .access("private")
        .base64Decode(false)
        .build();

    Assert.assertEquals(correct, options.getAsTask().toString());
  }
}
