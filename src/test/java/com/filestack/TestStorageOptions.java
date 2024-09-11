package org.filestack;

import org.filestack.internal.Util;
import java.io.IOException;
import java.util.Map;
import okhttp3.RequestBody;
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

  @Test
  public void testGetPartMap() throws IOException {
    StorageOptions options = new StorageOptions.Builder()
        .access("<access>")
        .container("<container>")
        .location("<location>")
        .path("<path>")
        .region("<region>")
        .build();

    Map<String, RequestBody> map = options.getAsPartMap();

    Assert.assertEquals("<access>", Util.partToString(map.get("store_access")));
    Assert.assertEquals("<container>", Util.partToString(map.get("store_container")));
    Assert.assertEquals("<location>", Util.partToString(map.get("store_location")));
    Assert.assertEquals("<path>", Util.partToString(map.get("store_path")));
    Assert.assertEquals("<region>", Util.partToString(map.get("store_region")));
  }

  @Test
  public void testGetPartMapLocationDefault() throws Exception {
    StorageOptions options = new StorageOptions.Builder().build();

    Map<String, RequestBody> map = options.getAsPartMap();

    Assert.assertEquals("s3", Util.partToString(map.get("store_location")));
  }
}
