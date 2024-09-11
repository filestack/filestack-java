package org.filestack.transforms;

import org.filestack.Config;
import org.filestack.internal.CdnService;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Test;

import static org.filestack.internal.MockResponse.success;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

  CdnService cdnService = mock(CdnService.class);

  @Test
  public void testGetContentExt() throws Exception {
    Config config = new Config("apikey");

    when(cdnService.transformExt("apikey","task", "https://example.com/example.txt"))
        .thenReturn(success(ResponseBody.create(MediaType.get("text/plain"), "Test Response")));

    Transform transform = new Transform(cdnService, config, "https://example.com/example.txt", true);
    transform.tasks.add(new TransformTask("task"));

    Assert.assertEquals("Test Response", transform.getContent().string());
  }

  @Test
  public void testGetContentHandle() throws Exception {
    Config config = new Config("apikey");

    when(cdnService.transform("task", "handle"))
        .thenReturn(success(ResponseBody.create(MediaType.get("text/plain"), "Test Response")));

    Transform transform = new Transform(cdnService, config, "handle", false);
    transform.tasks.add(new TransformTask("task"));

    Assert.assertEquals("Test Response", transform.getContent().string());
  }

  @Test
  public void testGetContentJson() throws Exception {
    Config config = new Config("apikey");
    String jsonString = "{'key': 'value'}";


    when(cdnService.transform("task", "handle"))
        .thenReturn(success(ResponseBody.create(MediaType.get("application/json"), jsonString)));

    Transform transform = new Transform(cdnService, config, "handle", false);
    transform.tasks.add(new TransformTask("task"));
    JsonObject jsonObject = transform.getContentJson();

    Assert.assertEquals("value", jsonObject.get("key").getAsString());
  }
}
