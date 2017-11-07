package com.filestack.transforms;

import com.filestack.Config;
import com.filestack.Helpers;
import com.filestack.internal.CdnService;
import com.filestack.internal.Networking;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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

  /** Set networking singletons to mocks. */
  @Before
  public void setup() {
    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Networking.setCdnService(mockCdnService);
  }

  /** Invalidate networking singletons. */
  @After
  public void teardown() {
    Networking.invalidate();
  }

  @Test
  public void testGetContentExt() throws Exception {
    Config config = new Config("apikey");

    Mockito
        .doReturn(Helpers.createRawCall("text/plain", "Test Response"))
        .when(Networking.getCdnService())
        .transformExt("apikey", "task", "https://example.com/example.txt");

    Transform transform = new Transform(config, "https://example.com/example.txt", true);
    transform.tasks.add(new TransformTask("task"));

    Assert.assertEquals("Test Response", transform.getContent().string());
  }

  @Test
  public void testGetContentHandle() throws Exception {
    Config config = new Config("apikey");

    Mockito
        .doReturn(Helpers.createRawCall("text/plain", "Test Response"))
        .when(Networking.getCdnService())
        .transform("task", "handle");

    Transform transform = new Transform(config, "handle", false);
    transform.tasks.add(new TransformTask("task"));

    Assert.assertEquals("Test Response", transform.getContent().string());
  }

  @Test
  public void testGetContentJson() throws Exception {
    Config config = new Config("apikey");
    String jsonString = "{'key': 'value'}";

    Mockito
        .doReturn(Helpers.createRawCall("application/json", jsonString))
        .when(Networking.getCdnService())
        .transform("task", "handle");

    Transform transform = new Transform(config, "handle", false);
    transform.tasks.add(new TransformTask("task"));
    JsonObject jsonObject = transform.getContentJson();

    Assert.assertEquals("value", jsonObject.get("key").getAsString());
  }
}
