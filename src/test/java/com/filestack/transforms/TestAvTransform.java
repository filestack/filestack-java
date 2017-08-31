package com.filestack.transforms;

import com.filestack.FileLink;
import com.filestack.StorageOptions;
import com.filestack.errors.InvalidArgumentException;
import com.filestack.transforms.tasks.AvTransformOptions;
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
  public void testStart() {
    FsService mockFsService = Mockito.mock(FsService.class);

    FileLink fileLink = new FileLink.Builder()
        .apiKey("apiKey")
        .handle("handle")
        .service(mockFsService)
        .build();

    MediaType mediaType = MediaType.parse("application/json");
    ResponseBody responseBody = ResponseBody.create(mediaType, "{}");
    Mockito
        .doReturn(Calls.response(responseBody))
        .when(mockFsService)
        .transform("video_convert=preset:mp4", "handle");

    AvTransformOptions avOptions = new AvTransformOptions.Builder()
        .preset("mp4")
        .build();

    fileLink.avTransform(avOptions).start();
  }

  @Test
  public void testIsReady() throws Exception {
    FsService mockFsService = Mockito.mock(FsService.class);

    Mockito
        .doAnswer(new Answer() {
          @Override
          public Call<ResponseBody> answer(InvocationOnMock invocation) throws Throwable {
            String handle = invocation.getArgument(1);
            boolean ready = handle.equals("ready");
            String json = "{'status': " + (ready ? "'completed'" : "'pending'") + "}";
            MediaType mediaType = MediaType.parse("application/json");
            return Calls.response(ResponseBody.create(mediaType, json));
          }
        })
        .when(mockFsService)
        .transform(Mockito.anyString(), Mockito.anyString());

    FileLink.Builder builder = new FileLink.Builder()
        .apiKey("apiKey")
        .service(mockFsService);

    FileLink ready = builder.handle("ready").build();
    FileLink pending = builder.handle("pending").build();

    AvTransformOptions avOptions = new AvTransformOptions.Builder()
        .preset("mp4")
        .build();

    Assert.assertTrue(ready.avTransform(avOptions).isReady());
    Assert.assertFalse(pending.avTransform(avOptions).isReady());
  }

  @Test
  public void testGetFilelink() throws Exception {
    FsService mockFsService = Mockito.mock(FsService.class);

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
        .when(mockFsService)
        .transform(Mockito.anyString(), Mockito.anyString());

    FileLink.Builder builder = new FileLink.Builder().apiKey("apiKey").service(mockFsService);

    FileLink ready = builder.handle("ready").build();
    FileLink pending = builder.handle("pending").build();

    AvTransformOptions avOptions = new AvTransformOptions.Builder().preset("mp4").build();

    FileLink converted = ready.avTransform(avOptions).getFilelink();
    Assert.assertEquals("handle", converted.getHandle());
    Assert.assertNull(pending.avTransform(avOptions).getFilelink());
  }
}
