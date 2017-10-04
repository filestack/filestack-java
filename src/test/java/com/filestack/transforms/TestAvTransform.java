package com.filestack.transforms;

import com.filestack.FileLink;
import com.filestack.StorageOptions;
import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidArgumentException;
import com.filestack.transforms.tasks.AvTransformOptions;
import com.filestack.util.FsCdnService;
import com.filestack.util.FsService;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit2.Call;
import retrofit2.mock.Calls;

public class TestAvTransform {

  @Test(expected = InvalidArgumentException.class)
  public void testConstructorException() {
    FileLink fileLink = new FileLink("apiKey", "handle");
    fileLink.avTransform(null);
  }

  @Test
  public void testConstructorNoStoreOpts() {
    AvTransformOptions avOptions = new AvTransformOptions.Builder()
        .preset("mp4")
        .build();

    FileLink fileLink = new FileLink("apiKey", "handle");
    TransformTask task = fileLink.avTransform(avOptions).tasks.get(0);

    Assert.assertEquals("video_convert=preset:mp4", task.toString());
  }

  @Test
  public void testConstructorStoreOpts() {
    StorageOptions storageOptions = new StorageOptions.Builder()
        .container("some-bucket")
        .build();

    AvTransformOptions avOptions = new AvTransformOptions.Builder()
        .preset("mp4")
        .build();

    FileLink fileLink = new FileLink("apiKey", "handle");
    TransformTask task = fileLink.avTransform(storageOptions, avOptions).tasks.get(0);

    Assert.assertEquals("video_convert=container:some-bucket,preset:mp4", task.toString());
  }

  @Test
  public void testGetFilelink() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsService mockFsService = new FsService(null, mockCdnService, null, null);

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

    FileLink.Builder builder = new FileLink.Builder().apiKey("apiKey").service(mockFsService);

    FileLink ready = builder.handle("ready").build();
    FileLink pending = builder.handle("pending").build();

    AvTransformOptions avOptions = new AvTransformOptions.Builder().preset("mp4").build();

    FileLink converted = ready.avTransform(avOptions).getFileLink();
    Assert.assertEquals("handle", converted.getHandle());
    Assert.assertNull(pending.avTransform(avOptions).getFileLink());
  }

  @Test(expected = InternalException.class)
  public void testGetFilelinkFail() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsService mockFsService = new FsService(null, mockCdnService, null, null);

    String json = "{'status':'failed'}";
    MediaType mediaType = MediaType.parse("application/json");
    ResponseBody responseBody = ResponseBody.create(mediaType, json);

    Mockito
        .doReturn(Calls.response(responseBody))
        .when(mockCdnService)
        .transform("video_convert=preset:mp4", "handle");

    FileLink fileLink = new FileLink.Builder()
        .apiKey("apiKey")
        .handle("handle")
        .service(mockFsService)
        .build();

    AvTransformOptions avOptions = new AvTransformOptions.Builder().preset("mp4").build();

    fileLink.avTransform(avOptions).getFileLink();
  }
}
