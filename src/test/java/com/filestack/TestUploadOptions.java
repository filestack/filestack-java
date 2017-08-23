package com.filestack;

import java.util.Map;
import okhttp3.RequestBody;
import org.junit.Assert;
import org.junit.Test;

public class TestUploadOptions {

  @Test
  public void testBuildingDefaults() {
    UploadOptions options = new UploadOptions.Builder()
        .region("<region>")
        .container("<container>")
        .path("<path>")
        .access("<access>")
        .build();

    Map<String, RequestBody> map = options.getMap();

    Assert.assertNotNull(map.get("store_location"));
    Assert.assertNotNull(map.get("store_region"));
    Assert.assertNotNull(map.get("store_container"));
    Assert.assertNotNull(map.get("store_path"));
    Assert.assertNotNull(map.get("store_access"));
    Assert.assertNotNull(map.get("multipart"));
  }

  @Test
  public void testBuildingComplete() {
    UploadOptions options = new UploadOptions.Builder()
        .location("<location>")
        .region("<region>")
        .container("<container>")
        .path("<path>")
        .access("<access>")
        .intelligent(true)
        .build();

    Map<String, RequestBody> map = options.getMap();

    Assert.assertNotNull(map.get("store_location"));
    Assert.assertNotNull(map.get("store_region"));
    Assert.assertNotNull(map.get("store_container"));
    Assert.assertNotNull(map.get("store_path"));
    Assert.assertNotNull(map.get("store_access"));
    Assert.assertNotNull(map.get("multipart"));
  }
}
