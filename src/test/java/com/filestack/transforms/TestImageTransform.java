package com.filestack.transforms;

import com.filestack.Config;
import com.filestack.internal.CdnService;
import com.filestack.internal.Networking;
import com.filestack.internal.responses.StoreResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import retrofit2.mock.Calls;

public class TestImageTransform {

  @Before
  public void setup() {
    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Networking.setCdnService(mockCdnService);
  }

  @After
  public void teardown() {
    Networking.invalidate();
  }

  @Test
  public void testDebugHandle() throws Exception {
    Config config = new Config("apiKey");
    ImageTransform transform = new ImageTransform(config, "handle", false);

    Mockito
        .doReturn(Calls.response(new JsonObject()))
        .when(Networking.getCdnService())
        .transformDebug("", "handle");

    Assert.assertNotNull(transform.debug());
  }

  @Test
  public void testDebugExternal() throws Exception {
    String url = "https://example.com/image.jpg";
    Config config = new Config("apiKey");
    ImageTransform transform = new ImageTransform(config, url, true);

    Mockito
        .doReturn(Calls.response(new JsonObject()))
        .when(Networking.getCdnService())
        .transformDebugExt("apiKey", "", url);

    Assert.assertNotNull(transform.debug());
  }

  @Test
  public void testStoreHandle() throws Exception {
    Config config = new Config("apiKey");
    ImageTransform transform = new ImageTransform(config, "handle", false);
    String jsonString = "{'url': 'https://cdn.filestackcontent.com/handle'}";
    Gson gson = new Gson();
    StoreResponse storeResponse = gson.fromJson(jsonString, StoreResponse.class);

    Mockito
        .doReturn(Calls.response(storeResponse))
        .when(Networking.getCdnService())
        .transformStore("store", "handle");

    Assert.assertNotNull(transform.store());
  }

  @Test
  public void testStoreExternal() throws Exception {
    String jsonString = "{'url': 'https://cdn.filestackcontent.com/handle'}";
    Gson gson = new Gson();
    StoreResponse storeResponse = gson.fromJson(jsonString, StoreResponse.class);
    String url = "https://example.com/image.jpg";

    Mockito
        .doReturn(Calls.response(storeResponse))
        .when(Networking.getCdnService())
        .transformStoreExt("apiKey", "store", url);

    Config config = new Config("apiKey");
    ImageTransform transform = new ImageTransform(config, url, true);

    Assert.assertNotNull(transform.store());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddNullTask() throws Exception {
    Config config = new Config("apiKey");
    ImageTransform transform = new ImageTransform(config, "handle", false);
    transform.addTask(null);
  }
}
