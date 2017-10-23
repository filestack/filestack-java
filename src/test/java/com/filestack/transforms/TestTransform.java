package com.filestack.transforms;

import com.filestack.FsFile;
import com.filestack.FilestackClient;
import com.filestack.Policy;
import com.filestack.Security;
import com.filestack.util.FsCdnService;
import com.filestack.util.FsService;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.mock.Calls;

public class TestTransform {
  private static final TransformTask TASK = new TransformTask("task");
  private static final String TASK_STRING = "task=option1:1,option2:1.0,option3:value,"
      + "option4:[1,1,1,1]";

  static {
    TASK.addOption("option1", 1);
    TASK.addOption("option2", 1.0);
    TASK.addOption("option3", "value");
    TASK.addOption("option4", new Integer[] {1, 1, 1, 1});
  }

  private static final Policy POLICY = new Policy.Builder().giveFullAccess().build();
  private static final Security SECURITY = Security.createNew(POLICY, "appSecret");

  @Test
  public void testUrlHandle() {
    FsFile fsFile = new FsFile("apiKey", "handle");

    Transform transform = new Transform(fsFile);
    transform.tasks.add(TASK);

    String correctUrl = FsCdnService.URL + TASK_STRING + "/" + "handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlExternal() {
    FilestackClient client = new FilestackClient("apiKey");

    String sourceUrl = "https://example.com/image.jpg";
    Transform transform = new Transform(client, sourceUrl);
    transform.tasks.add(TASK);

    String correctUrl = FsCdnService.URL + "apiKey/" + TASK_STRING + "/" + sourceUrl;
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlSecurity() {
    FsFile fsFile = new FsFile("apikey", "handle", SECURITY);

    Transform transform = new Transform(fsFile);
    transform.tasks.add(TASK);

    String correctUrl = FsCdnService.URL + "security=policy:" + SECURITY.getPolicy() + ","
        + "signature:" + SECURITY.getSignature() + "/" + TASK_STRING + "/handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlMultipleTasks() {
    FsFile fsFile = new FsFile("apikey", "handle");

    Transform transform = new Transform(fsFile);
    transform.tasks.add(TASK);
    transform.tasks.add(TASK);

    String correctUrl = FsCdnService.URL + TASK_STRING + "/" + TASK_STRING + "/handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlTaskWithoutOptions() {
    FsFile fsFile = new FsFile("apikey", "handle");

    Transform transform = new Transform(fsFile);
    transform.tasks.add(new TransformTask("task"));

    String correctUrl = FsCdnService.URL + "task/handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testGetContentExt() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsService mockFsService = new FsService(null, mockCdnService, null, null);

    MediaType mediaType = MediaType.parse("application/octet-stream");
    ResponseBody responseBody = ResponseBody.create(mediaType, "Test Response");
    Call call = Calls.response(responseBody);
    Mockito.doReturn(call)
        .when(mockCdnService)
        .transformExt("apiKey", "task", "https://example.com/");

    FilestackClient client = new FilestackClient("apiKey", null, mockFsService);

    Transform transform = new Transform(client, "https://example.com/");
    transform.tasks.add(new TransformTask("task"));

    Assert.assertEquals("Test Response", transform.getContent().string());
  }

  @Test
  public void testGetContentHandle() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsService mockFsService = new FsService(null, mockCdnService, null, null);

    MediaType mediaType = MediaType.parse("application/octet-stream");
    ResponseBody responseBody = ResponseBody.create(mediaType, "Test Response");
    Call call = Calls.response(responseBody);
    Mockito.doReturn(call)
        .when(mockCdnService)
        .transform("task", "handle");

    FsFile fsFile = new FsFile.Builder()
        .apiKey("apiKey")
        .handle("handle")
        .service(mockFsService)
        .build();

    Transform transform = new Transform(fsFile);
    transform.tasks.add(new TransformTask("task"));

    Assert.assertEquals("Test Response", transform.getContent().string());
  }

  @Test
  public void testGetContentJson() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsService mockFsService = new FsService(null, mockCdnService, null, null);

    String jsonString = "{"
        + "'key': 'value'"
        + "}";

    MediaType mediaType = MediaType.parse("application/json");
    ResponseBody responseBody = ResponseBody.create(mediaType, jsonString);
    Call call = Calls.response(responseBody);
    Mockito.doReturn(call)
        .when(mockCdnService)
        .transform("task", "handle");

    FsFile fsFile = new FsFile.Builder()
        .apiKey("apiKey")
        .handle("handle")
        .service(mockFsService)
        .build();

    Transform transform = new Transform(fsFile);
    transform.tasks.add(new TransformTask("task"));

    JsonObject jsonObject = transform.getContentJson();

    Assert.assertEquals("value", jsonObject.get("key").getAsString());
  }
}
