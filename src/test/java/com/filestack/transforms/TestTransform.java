package com.filestack.transforms;

import com.filestack.FileLink;
import com.filestack.FilestackClient;
import com.filestack.Policy;
import com.filestack.Security;
import com.filestack.util.FsCdnService;
import org.junit.Assert;
import org.junit.Test;

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
    FileLink fileLink = new FileLink("apiKey", "handle");

    Transform transform = new Transform(fileLink);
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
    FileLink fileLink = new FileLink("apikey", "handle", SECURITY);

    Transform transform = new Transform(fileLink);
    transform.tasks.add(TASK);

    String correctUrl = FsCdnService.URL + "security=policy:" + SECURITY.getPolicy() + ","
        + "signature:" + SECURITY.getSignature() + "/" + TASK_STRING + "/handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlMultipleTasks() {
    FileLink fileLink = new FileLink("apikey", "handle");

    Transform transform = new Transform(fileLink);
    transform.tasks.add(TASK);
    transform.tasks.add(TASK);

    String correctUrl = FsCdnService.URL + TASK_STRING + "/" + TASK_STRING + "/handle";
    Assert.assertEquals(correctUrl, transform.url());
  }

  @Test
  public void testUrlTaskWithoutOptions() {
    FileLink fileLink = new FileLink("apikey", "handle");

    Transform transform = new Transform(fileLink);
    transform.tasks.add(new TransformTask("task"));

    String correctUrl = FsCdnService.URL + "task/handle";
    Assert.assertEquals(correctUrl, transform.url());
  }
}
