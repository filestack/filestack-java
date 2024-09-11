package org.filestack.transforms;

import org.filestack.Config;
import org.filestack.FileLink;
import org.filestack.StorageOptions;
import org.filestack.internal.BaseService;
import org.filestack.internal.CdnService;
import org.filestack.internal.MockResponse;
import org.filestack.transforms.tasks.AvTransformOptions;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.filestack.UtilsKt.fileLink;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAvTransform {


  CdnService cdnService = mock(CdnService.class);
  BaseService baseService = mock(BaseService.class);

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorException() {
    Config config = new Config("apiKey");
    AvTransform transform = new AvTransform(cdnService, config, "handle", null, null);
  }

  @Test
  public void testConstructorNoStoreOpts() {
    AvTransformOptions avOpts = new AvTransformOptions.Builder()
        .preset("mp4")
        .build();

    Config config = new Config("apiKey");
    TransformTask task = new AvTransform(cdnService, config, "handle", null, avOpts).tasks.get(0);

    Assert.assertEquals("video_convert=preset:mp4", task.toString());
  }

  @Test
  public void testConstructorStoreOpts() {
    StorageOptions storeOpts = new StorageOptions.Builder()
        .container("some-bucket")
        .build();

    AvTransformOptions avOpts = new AvTransformOptions.Builder()
        .preset("mp4")
        .build();

    Config config = new Config("apiKey");
    TransformTask task = new AvTransform(cdnService, config, "handle", storeOpts, avOpts).tasks.get(0);

    Assert.assertEquals("video_convert=container:some-bucket,preset:mp4", task.toString());
  }

  @Test
  public void testGetFilelink() throws Exception {
    ResponseBody readyBody = ResponseBody.create(
        MediaType.get("application/json"),
        "{\n" +
            "  \"status\": \"completed\",\n" +
            "  \"data\": {\n" +
            "    \"url\": \"https://cdn.filestackcontent.com/handle\"\n" +
            "  }\n" +
            "}"
    );

    ResponseBody notReadyBody = ResponseBody.create(
        MediaType.get("application/json"),
        "{\n" +
            "  \"status\": \"pending\",\n" +
            "  \"data\": {\n" +
            "    \"url\": \"https://cdn.filestackcontent.com/handle\"\n" +
            "  }\n" +
            "}"
    );

    when(cdnService.transform(anyString(), eq("pending")))
        .thenReturn(MockResponse.<ResponseBody>success(notReadyBody));

    when(cdnService.transform(anyString(), eq("ready")))
        .thenReturn(MockResponse.<ResponseBody>success(readyBody));

    Config config = new Config("apiKey");
    FileLink ready = fileLink(config, cdnService, baseService, "ready");
    FileLink pending = fileLink(config, cdnService, baseService, "pending");

    AvTransformOptions avOptions = new AvTransformOptions.Builder().preset("mp4").build();

    FileLink converted = ready.avTransform(avOptions).getFileLink();
    Assert.assertEquals("handle", converted.getHandle());
    Assert.assertNull(pending.avTransform(avOptions).getFileLink());
  }

  @Test(expected = IOException.class)
  public void testGetFilelinkFail() throws Exception {

    ResponseBody body = ResponseBody.create(
        MediaType.get("application/json"),
        "{'status':'failed'}"
    );

    when(cdnService.transform("video_convert=preset:mp4", "handle"))
        .thenReturn(MockResponse.<ResponseBody>success(body));

    Config config = new Config("apiKey");
    FileLink fileLink = fileLink(config, cdnService, baseService, "handle");

    AvTransformOptions avOptions = new AvTransformOptions.Builder().preset("mp4").build();

    fileLink.avTransform(avOptions).getFileLink();
  }
}
