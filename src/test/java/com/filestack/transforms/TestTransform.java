package com.filestack.transforms;

import com.filestack.Config;
import com.filestack.util.CdnService;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.mock.Calls;

public class TestTransform {
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
    Config config = new Config.Builder().apiKey("apiKey").build();
    Transform transform = new Transform(config, "handle", false);
    transform.tasks.add(task);

    String correctUrl = CdnService.URL + TASK_STRING + "/" + "handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlExternal() {
    Config config = new Config.Builder().apiKey("apiKey").build();
    String sourceUrl = "https://example.com/image.jpg";
    Transform transform = new Transform(config, sourceUrl, true);
    transform.tasks.add(task);

    String correctUrl = CdnService.URL + "apiKey/" + TASK_STRING + "/" + sourceUrl;
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlSecurity() {
    Config config = new Config.Builder()
        .security("policy", "signature")
        .apiKey("apiKey")
        .build();
    Transform transform = new Transform(config, "handle", false);
    transform.tasks.add(task);

    String correctUrl = CdnService.URL + "security=policy:policy,signature:signature/"
        + TASK_STRING + "/handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlMultipleTasks() {
    Config config = new Config.Builder().apiKey("apiKey").build();
    Transform transform = new Transform(config, "handle", false);
    transform.tasks.add(task);
    transform.tasks.add(task);

    String correctUrl = CdnService.URL + TASK_STRING + "/" + TASK_STRING + "/handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlTaskWithoutOptions() {
    Config config = new Config.Builder().apiKey("apiKey").build();
    Transform transform = new Transform(config, "handle", false);
    transform.tasks.add(new TransformTask("task"));

    String correctUrl = CdnService.URL + "task/handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testGetContentExt() throws Exception {
    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Config config = new Config.Builder()
        .apiKey("apiKey")
        .cdnService(mockCdnService)
        .build();

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
    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Config config = new Config.Builder()
        .apiKey("apiKey")
        .cdnService(mockCdnService)
        .build();

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
    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Config config = new Config.Builder()
        .apiKey("apiKey")
        .cdnService(mockCdnService)
        .build();

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
