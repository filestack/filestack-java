package com.filestack.transforms;

import com.filestack.FsConfig;
import com.filestack.FsFile;
import com.filestack.StorageOptions;
import com.filestack.transforms.tasks.AvTransformOptions;
import com.filestack.util.FsCdnService;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit2.Call;
import retrofit2.mock.Calls;

import java.io.IOException;

public class TestAvTransform {
  private static final FsConfig.Builder configBuilder = new FsConfig.Builder().apiKey("apikey");
  private static final FsConfig defaultConfig = configBuilder.build();

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorException() {
    AvTransform transform = new AvTransform(defaultConfig, "handle", null, null);
  }

  @Test
  public void testConstructorNoStoreOpts() {
    AvTransformOptions avOpts = new AvTransformOptions.Builder()
        .preset("mp4")
        .build();

    TransformTask task = new AvTransform(defaultConfig, "handle", null, avOpts).tasks.get(0);

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

    TransformTask task = new AvTransform(defaultConfig, "handle", storeOpts, avOpts).tasks.get(0);

    Assert.assertEquals("video_convert=container:some-bucket,preset:mp4", task.toString());
  }

  @Test
  public void testGetFilelink() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsConfig config = configBuilder.cdnService(mockCdnService).build();

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
        .when(mockCdnService)
        .transform(Mockito.anyString(), Mockito.anyString());

    FsFile ready = new FsFile(config, "ready");
    FsFile pending = new FsFile(config, "pending");

    AvTransformOptions avOptions = new AvTransformOptions.Builder().preset("mp4").build();

    FsFile converted = ready.avTransform(avOptions).getFileLink();
    Assert.assertEquals("handle", converted.getHandle());
    Assert.assertNull(pending.avTransform(avOptions).getFileLink());
  }

  @Test(expected = IOException.class)
  public void testGetFilelinkFail() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsConfig config = configBuilder.cdnService(mockCdnService).build();

    String json = "{'status':'failed'}";
    MediaType mediaType = MediaType.parse("application/json");
    ResponseBody responseBody = ResponseBody.create(mediaType, json);

    Mockito
        .doReturn(Calls.response(responseBody))
        .when(mockCdnService)
        .transform("video_convert=preset:mp4", "handle");

    FsFile fsFile = new FsFile(config, "handle");

    AvTransformOptions avOptions = new AvTransformOptions.Builder().preset("mp4").build();

    fsFile.avTransform(avOptions).getFileLink();
  }
}
