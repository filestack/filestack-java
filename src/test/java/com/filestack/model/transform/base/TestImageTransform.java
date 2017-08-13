package com.filestack.model.transform.base;

import static com.filestack.util.MockConstants.API_KEY;
import static com.filestack.util.MockConstants.FILE_LINK;
import static com.filestack.util.MockConstants.FS_CLIENT;
import static com.filestack.util.MockConstants.HANDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.filestack.model.FileLink;
import com.filestack.model.transform.tasks.StoreOptions;
import com.filestack.util.FilestackService;
import com.filestack.util.MockInterceptor;
import com.filestack.util.Networking;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import org.junit.AfterClass;
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
  public void testDebug() throws IOException {
    ImageTransform transform = FILE_LINK.imageTransform();
    JsonObject debugResponse = transform.debug();
    String message = "Debug response was null";
    assertTrue(message, debugResponse != null);
  }

  @Test
  public void testDebugUrl() throws IOException {
    FilestackService.Process processService = Networking.getProcessService();

    String correct = FilestackService.Process.URL + "debug/" + RESIZE_TASK_STRING
        + "/" + HANDLE;
    String output = processService.debug(RESIZE_TASK_STRING, HANDLE)
        .request()
        .url()
        .toString();

    String message = String.format("Debug URL malformed\nCorrect: %s\nOutput:  %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  @Test
  public void testDebugExternal() throws IOException {
    ImageTransform transform = FS_CLIENT.imageTransform(SOURCE);
    JsonObject debugResponse = transform.debug();
    String message = "External debug response was null";
    assertTrue(message, debugResponse != null);
  }

  @Test
  public void testDebugExternalUrl() throws IOException {
    FilestackService.Process processService = Networking.getProcessService();

    // Retrofit will return the URL with some characters escaped
    // We check for a string with the encoded source
    String correct = FilestackService.Process.URL + API_KEY + "/debug/" + RESIZE_TASK_STRING
        + "/" + ENCODED_SOURCE;
    String output = processService.debugExternal(API_KEY, RESIZE_TASK_STRING, SOURCE)
        .request()
        .url()
        .toString();

    String message = String.format("External debug URL malformed\nCorrect: %s\nOutput:  %s",
        correct, output);
    assertTrue(message, output.equals(correct));
  }

  /**
   * Tests conversion of JSON response into POJO and creation of a new {@link FileLink} object.
   */
  @Test
  public void testStore() throws IOException {
    StoreOptions storeOptions = new StoreOptions();
    FilestackService.Process processService = Networking.getProcessService();
    FilestackService.Process.StoreResponse storeResponse;
    storeResponse = processService.store(storeOptions.toString(), FILE_LINK.getHandle())
        .execute()
        .body();

    assertNotNull(storeResponse);
    assertTrue(storeResponse.getContainer().equals("my_bucket"));
    assertTrue(storeResponse.getKey().equals("NEW_HANDLE_some_file.jpg"));
    assertTrue(storeResponse.getFilename().equals("some_file.jpg"));
    assertTrue(storeResponse.getType().equals("image/jpeg"));
    assertEquals(storeResponse.getWidth(), 1000);
    assertEquals(storeResponse.getHeight(), 1000);
    assertEquals(storeResponse.getSize(), 200000);

    ImageTransform transform = FILE_LINK.imageTransform();
    FileLink filelink = transform.store();
    assertTrue(filelink.getHandle().equals("NEW_HANDLE"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddNullTask() throws IOException {
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
