package com.filestack.transforms;

import com.filestack.Config;
import com.filestack.StorageOptions;
import com.filestack.internal.CdnService;
import com.filestack.internal.MockResponse;
import com.filestack.internal.responses.StoreResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
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
    StorageOptions storageOptions = new StorageOptions.Builder()
        .filename("my_filename.mp4")
        .build();

    when(cdnService.transformStore("store=filename:my_filename.mp4,location:s3", "handle"))
        .thenReturn(MockResponse.<StoreResponse>success(storeResponse));

    Assert.assertNotNull(transform.store(storageOptions));
  }

  @Test
  public void testStoreExternal() throws Exception {
    String jsonString = "{'url': 'https://cdn.filestackcontent.com/handle'}";
    Gson gson = new Gson();
    StoreResponse storeResponse = gson.fromJson(jsonString, StoreResponse.class);
    String url = "https://example.com/image.jpg";
    StorageOptions storageOptions = new StorageOptions.Builder()
        .filename("my_filename.mp4")
        .build();

    when(cdnService.transformStoreExt("apiKey","store=filename:my_filename.mp4,location:s3", url))
        .thenReturn(MockResponse.success(storeResponse));

    Config config = new Config("apiKey");
    ImageTransform transform = new ImageTransform(config, cdnService, url, true);

    Assert.assertNotNull(transform.store(storageOptions));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddNullTask() throws Exception {
    Config config = new Config("apiKey");
    ImageTransform transform = new ImageTransform(config, cdnService,"handle", false);
    transform.addTask(null);
  }
}
