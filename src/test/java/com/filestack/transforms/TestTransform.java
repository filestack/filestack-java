package com.filestack.transforms;

import com.filestack.FsConfig;
import com.filestack.util.FsCdnService;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.mock.Calls;

public class TestTransform {
  private static final FsConfig.Builder configBuilder = new FsConfig.Builder().apiKey("apikey");
  private static final FsConfig defaultConfig = configBuilder.build();
  
  private static final TransformTask task = new TransformTask("task");
  private static final String TASK_STRING = "task=option1:1,option2:1.0,option3:value,"
      + "option4:[1,1,1,1]";

  static {
    task.addOption("option1", 1);
    task.addOption("option2", 1.0);
    task.addOption("option3", "value");
    task.addOption("option4", new Integer[] {1, 1, 1, 1});
  }

  @Test
  public void testUrlHandle() {
    Transform transform = new Transform(defaultConfig, "handle", false);
    transform.tasks.add(task);

    String correctUrl = FsCdnService.URL + TASK_STRING + "/" + "handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlExternal() {
    String sourceUrl = "https://example.com/image.jpg";
    Transform transform = new Transform(defaultConfig, sourceUrl, true);
    transform.tasks.add(task);

    String correctUrl = FsCdnService.URL + "apiKey/" + TASK_STRING + "/" + sourceUrl;
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlSecurity() {
    FsConfig config = configBuilder.security("policy", "signature").build();
    Transform transform = new Transform(config, "handle", false);
    transform.tasks.add(task);

    String correctUrl = FsCdnService.URL + "security=policy:policy,signature:signature/"
        + TASK_STRING + "/handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlMultipleTasks() {
    Transform transform = new Transform(defaultConfig, "handle", false);
    transform.tasks.add(task);
    transform.tasks.add(task);

    String correctUrl = FsCdnService.URL + TASK_STRING + "/" + TASK_STRING + "/handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlTaskWithoutOptions() {
    Transform transform = new Transform(defaultConfig, "handle", false);
    transform.tasks.add(new TransformTask("task"));

    String correctUrl = FsCdnService.URL + "task/handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testGetContentExt() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsConfig config = configBuilder.cdnService(mockCdnService).build();

    MediaType mediaType = MediaType.parse("application/octet-stream");
    ResponseBody responseBody = ResponseBody.create(mediaType, "Test Response");
    Call call = Calls.response(responseBody);
    Mockito.doReturn(call)
        .when(mockCdnService)
        .transformExt("apiKey", "task", "https://example.com/");

    Transform transform = new Transform(config, "https://example.com/", true);

    transform.tasks.add(new TransformTask("task"));

    Assert.assertEquals("Test Response", transform.getContent().string());
  }

  @Test
  public void testGetContentHandle() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsConfig config = configBuilder.cdnService(mockCdnService).build();

    MediaType mediaType = MediaType.parse("application/octet-stream");
    ResponseBody responseBody = ResponseBody.create(mediaType, "Test Response");
    Call call = Calls.response(responseBody);
    Mockito.doReturn(call).when(mockCdnService).transform("task", "handle");

    Transform transform = new Transform(config, "handle", false);

    transform.tasks.add(new TransformTask("task"));

    Assert.assertEquals("Test Response", transform.getContent().string());
  }

  @Test
  public void testGetContentJson() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsConfig config = configBuilder.cdnService(mockCdnService).build();

    String jsonString = "{"
        + "'key': 'value'"
        + "}";

    MediaType mediaType = MediaType.parse("application/json");
    ResponseBody responseBody = ResponseBody.create(mediaType, jsonString);
    Call call = Calls.response(responseBody);
    Mockito.doReturn(call).when(mockCdnService).transform("task", "handle");

    Transform transform = new Transform(config, "handle", false);
    transform.tasks.add(new TransformTask("task"));

    JsonObject jsonObject = transform.getContentJson();

    Assert.assertEquals("value", jsonObject.get("key").getAsString());
  }
}
