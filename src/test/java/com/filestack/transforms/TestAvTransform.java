package com.filestack.transforms;

import com.filestack.Config;
import com.filestack.FileLink;
import com.filestack.Helpers;
import com.filestack.StorageOptions;
import com.filestack.internal.CdnService;
import com.filestack.internal.Networking;
import com.filestack.transforms.tasks.AvTransformOptions;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit2.Call;
import retrofit2.mock.Calls;

import java.io.IOException;

public class TestAvTransform {

  @Before
  public void setup() {
    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Networking.setCdnService(mockCdnService);
  }

  @After
  public void teardown() {
    Networking.invalidate();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorException() {
    Config config = new Config("apiKey");
    AvTransform transform = new AvTransform(config, "handle", null, null);
  }

  @Test
  public void testConstructorNoStoreOpts() {
    AvTransformOptions avOpts = new AvTransformOptions.Builder()
        .preset("mp4")
        .build();

    Config config = new Config("apiKey");
    TransformTask task = new AvTransform(config, "handle", null, avOpts).tasks.get(0);

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
    TransformTask task = new AvTransform(config, "handle", storeOpts, avOpts).tasks.get(0);

    Assert.assertEquals("video_convert=container:some-bucket,preset:mp4", task.toString());
  }

  @Test
  public void testGetFilelink() throws Exception {
    Mockito
        .doAnswer(new Answer() {
          @Override
          public Call<ResponseBody> answer(InvocationOnMock invocation) throws Throwable {
            boolean ready = invocation.getArgument(1).equals("ready");
            String json = "{'status':" + (ready ? "'completed'" : "'pending'") + ","
                + "'data': {'url': 'https://cdn.filestackcontent.com/handle'}}";
            MediaType mediaType = MediaType.parse("application/json");
            return Calls.response(ResponseBody.create(mediaType, json));
          }
        })
        .when(Networking.getCdnService())
        .transform(Mockito.anyString(), Mockito.anyString());

    Config config = new Config("apiKey");
    FileLink ready = new FileLink(config, "ready");
    FileLink pending = new FileLink(config, "pending");

    AvTransformOptions avOptions = new AvTransformOptions.Builder().preset("mp4").build();

    FileLink converted = ready.avTransform(avOptions).getFileLink();
    Assert.assertEquals("handle", converted.getHandle());
    Assert.assertNull(pending.avTransform(avOptions).getFileLink());
  }

  @Test(expected = IOException.class)
  public void testGetFilelinkFail() throws Exception {
    Mockito
        .doReturn(Helpers.createRawCall("application/json", "{'status':'failed'}"))
        .when(Networking.getCdnService())
        .transform("video_convert=preset:mp4", "handle");

    Config config = new Config("apiKey");
    FileLink fileLink = new FileLink(config, "handle");

    AvTransformOptions avOptions = new AvTransformOptions.Builder().preset("mp4").build();

    fileLink.avTransform(avOptions).getFileLink();
  }
}
