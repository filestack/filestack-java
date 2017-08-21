package com.filestack.transforms;

import static com.filestack.util.MockConstants.API_KEY;
import static com.filestack.util.MockConstants.FILE_LINK;
import static com.filestack.util.MockConstants.FS_CLIENT;
import static com.filestack.util.MockConstants.HANDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.filestack.FileLink;
import com.filestack.FilestackClient;
import com.filestack.responses.StoreResponse;
import com.filestack.transforms.tasks.StoreOptions;
import com.filestack.util.FsCdnService;
import com.filestack.util.MockInterceptor;
import com.filestack.util.Networking;

import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestImageTransform {
  private static final String RESIZE_TASK_STRING = "resize=width:100,height:100";
  private static final String SOURCE = "https://example.com/image.jpg";
  private static final String ENCODED_SOURCE = "https:%2F%2Fexample.com%2Fimage.jpg";

  /**
   * Set a custom httpClient for our testing.
   * This custom client has an added interceptor to create mock responses.
   */
  @BeforeClass
  public static void setup() {
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new MockInterceptor())
        .build();
    Networking.setCustomClient(client);
  }

  @Test
  public void testDebug() throws Exception {
    FileLink fileLink = new FileLink("apiKey", "handle");
    Assert.assertNotNull(fileLink.imageTransform().debug());
    FilestackClient client = new FilestackClient("apiKey");
    Assert.assertNotNull(client.imageTransform("https://example.com/image.jpg").debug());
  }

  @Test
  public void testDebugUrl() throws Exception {
    FsCdnService fsCdnService = Networking.getFsCdnService();

    String correct = FsCdnService.URL + "debug/" + RESIZE_TASK_STRING
        + "/" + HANDLE;
    String output = fsCdnService.transformDebug(RESIZE_TASK_STRING, HANDLE)
        .request()
        .url()
        .toString();

    String message = String.format("Debug URL malformed\nCorrect: %s\nOutput:  %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testDebugExternal() throws Exception {
    ImageTransform transform = FS_CLIENT.imageTransform(SOURCE);
    JsonObject debugResponse = transform.debug();
    String message = "External debug response was null";
    assertTrue(message, debugResponse != null);
  }

  @Test
  public void testDebugExternalUrl() throws Exception {
    FsCdnService fsCdnService = Networking.getFsCdnService();

    // Retrofit will return the URL with some characters escaped
    // We check for a string with the encoded source
    String correct = FsCdnService.URL + API_KEY + "/debug/" + RESIZE_TASK_STRING
        + "/" + ENCODED_SOURCE;
    String output = fsCdnService.transformDebugExt(API_KEY, RESIZE_TASK_STRING, SOURCE)
        .request()
        .url()
        .toString();

    String message = String.format("External debug URL malformed\nCorrect: %s\nOutput:  %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testStore() throws Exception {
    FileLink fileLink = new FileLink("apiKey", "handle");
    Assert.assertNotNull(fileLink.imageTransform().store());
    FilestackClient client = new FilestackClient("apiKey");
    Assert.assertNotNull(client.imageTransform("https://example.com/image.jpg").store());
  }

  @Test(expected = NullPointerException.class)
  public void testAddNullTask() throws Exception {
    ImageTransform transform = FILE_LINK.imageTransform();
    transform.addTask(null);
  }

  /**
   * Clear changes to {@link Networking Networking} class since it's a shared resource.
   */
  @AfterClass
  public static void teardown() {
    Networking.removeCustomClient();
  }
}
