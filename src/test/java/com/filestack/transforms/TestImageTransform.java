package org.filestack.transforms;

import org.filestack.Config;
import org.filestack.internal.CdnService;
import org.filestack.internal.MockResponse;
import org.filestack.internal.responses.StoreResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class TestImageTransform {

  final CdnService cdnService = Mockito.mock(CdnService.class);

  @Test
  public void testDebugHandle() throws Exception {
    Config config = new Config("apiKey");
    ImageTransform transform = new ImageTransform(config, cdnService,"handle", false);

    when(cdnService.transformDebug("", "handle"))
        .thenReturn(MockResponse.<JsonObject>success(new JsonObject()));

    Assert.assertNotNull(transform.debug());
  }

  @Test
  public void testDebugExternal() throws Exception {
    String url = "https://example.com/image.jpg";
    Config config = new Config("apiKey");
    ImageTransform transform = new ImageTransform(config, cdnService,  url, true);

    when(cdnService.transformDebugExt("apiKey", "", url))
        .thenReturn(MockResponse.<JsonObject>success(new JsonObject()));

    Assert.assertNotNull(transform.debug());
  }

  @Test
  public void testStoreHandle() throws Exception {
    Config config = new Config("apiKey");
    ImageTransform transform = new ImageTransform(config, cdnService,"handle", false);
    String jsonString = "{'url': 'https://cdn.filestackcontent.com/handle'}";
    Gson gson = new Gson();
    StoreResponse storeResponse = gson.fromJson(jsonString, StoreResponse.class);

    when(cdnService.transformStore("store", "handle"))
        .thenReturn(MockResponse.<StoreResponse>success(storeResponse));

    Assert.assertNotNull(transform.store());
  }

  @Test
  public void testStoreExternal() throws Exception {
    String jsonString = "{'url': 'https://cdn.filestackcontent.com/handle'}";
    Gson gson = new Gson();
    StoreResponse storeResponse = gson.fromJson(jsonString, StoreResponse.class);
    String url = "https://example.com/image.jpg";

    when(cdnService.transformStoreExt("apiKey","store", url))
        .thenReturn(MockResponse.<StoreResponse>success(storeResponse));

    Config config = new Config("apiKey");
    ImageTransform transform = new ImageTransform(config, cdnService, url, true);

    Assert.assertNotNull(transform.store());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddNullTask() throws Exception {
    Config config = new Config("apiKey");
    ImageTransform transform = new ImageTransform(config, cdnService,"handle", false);
    transform.addTask(null);
  }
}
