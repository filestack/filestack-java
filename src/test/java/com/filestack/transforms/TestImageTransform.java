package com.filestack.transforms;

import com.filestack.Config;
import com.filestack.internal.CdnService;
import com.filestack.internal.Networking;
import com.filestack.internal.responses.StoreResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import retrofit2.mock.Calls;

public class TestImageTransform {
  @Test
  public void testDebugUrl() throws Exception {
    String taskString = "resize=width:100,height:100";
    String correctUrl = CdnService.URL + "debug/" + taskString + "/handle";
    String outputUrl = Networking.getCdnService().transformDebug(taskString, "handle")
        .request()
        .url()
        .toString();

    Assert.assertEquals(correctUrl, outputUrl);
  }

  @Test
  public void testDebugUrlExternal() throws Exception {
    String taskString = "resize=width:100,height:100";
    String url = "https://example.com/image.jpg";
    String encodedUrl = "https:%2F%2Fexample.com%2Fimage.jpg";

    // Retrofit will return the URL with some characters escaped, so check for encoded version
    String correctUrl = CdnService.URL + "apiKey/debug/" + taskString + "/" + encodedUrl;
    String outputUrl = Networking.getCdnService().transformDebugExt("apiKey", taskString, url)
        .request()
        .url()
        .toString();

    Assert.assertEquals(correctUrl, outputUrl);
  }

  @Test
  public void testDebugHandle() throws Exception {
    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Config config = new Config.Builder()
        .apiKey("apiKey")
        .cdnService(mockCdnService)
        .build();

    ImageTransform transform = new ImageTransform(config, "handle", false);

    Mockito.doReturn(Calls.response(new JsonObject()))
        .when(mockCdnService)
        .transformDebug("", "handle");

    Assert.assertNotNull(transform.debug());
  }

  @Test
  public void testDebugExternal() throws Exception {
    String url = "https://example.com/image.jpg";
    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Config config = new Config.Builder()
        .apiKey("apiKey")
        .cdnService(mockCdnService)
        .build();

    ImageTransform transform = new ImageTransform(config, url, true);

    Mockito.doReturn(Calls.response(new JsonObject()))
        .when(mockCdnService)
        .transformDebugExt("apiKey", "", url);

    Assert.assertNotNull(transform.debug());
  }

  @Test
  public void testStoreHandle() throws Exception {
    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Config config = new Config.Builder()
        .apiKey("apiKey")
        .cdnService(mockCdnService)
        .build();

    ImageTransform transform = new ImageTransform(config, "handle", false);

    String jsonString = "{'url': 'https://cdn.filestackcontent.com/handle'}";
    Gson gson = new Gson();
    StoreResponse storeResponse = gson.fromJson(jsonString, StoreResponse.class);

    Mockito.doReturn(Calls.response(storeResponse))
        .when(mockCdnService)
        .transformStore("store", "handle");

    Assert.assertNotNull(transform.store());
  }

  @Test
  public void testStoreExternal() throws Exception {
    String jsonString = "{'url': 'https://cdn.filestackcontent.com/handle'}";
    Gson gson = new Gson();
    StoreResponse storeResponse = gson.fromJson(jsonString, StoreResponse.class);
    String url = "https://example.com/image.jpg";

    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Mockito.doReturn(Calls.response(storeResponse))
        .when(mockCdnService)
        .transformStoreExt("apiKey", "store", url);

    Config config = new Config.Builder()
        .apiKey("apiKey")
        .cdnService(mockCdnService)
        .build();
    ImageTransform transform = new ImageTransform(config, url, true);

    Assert.assertNotNull(transform.store());
  }

  @Test(expected = NullPointerException.class)
  public void testAddNullTask() throws Exception {
    Config config = new Config.Builder().apiKey("apiKey").build();
    ImageTransform transform = new ImageTransform(config, "handle", false);
    transform.addTask(null);
  }
}
