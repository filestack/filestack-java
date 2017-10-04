package com.filestack.transforms;

import com.filestack.FileLink;
import com.filestack.FilestackClient;
import com.filestack.util.FsCdnService;
import com.filestack.util.FsService;
import com.filestack.util.responses.StoreResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import retrofit2.mock.Calls;

public class TestImageTransform {

  @Test
  public void testDebugUrl() throws Exception {
    FsService fsService = new FsService();

    String taskString = "resize=width:100,height:100";
    String correctUrl = FsCdnService.URL + "debug/" + taskString + "/handle";
    String outputUrl = fsService.cdn().transformDebug(taskString, "handle")
        .request()
        .url()
        .toString();

    Assert.assertEquals(correctUrl, outputUrl);
  }

  @Test
  public void testDebugUrlExternal() throws Exception {
    FsService fsService = new FsService();

    String taskString = "resize=width:100,height:100";
    String url = "https://example.com/image.jpg";
    String encodedUrl = "https:%2F%2Fexample.com%2Fimage.jpg";

    // Retrofit will return the URL with some characters escaped, so check for encoded version
    String correctUrl = FsCdnService.URL + "apiKey/debug/" + taskString + "/" + encodedUrl;
    String outputUrl = fsService.cdn().transformDebugExt("apiKey", taskString, url)
        .request()
        .url()
        .toString();

    Assert.assertEquals(correctUrl, outputUrl);
  }

  @Test
  public void testDebugHandle() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsService mockFsService = new FsService(null, mockCdnService, null, null);
    FileLink fileLink = new FileLink.Builder()
        .apiKey("apiKey")
        .handle("handle")
        .service(mockFsService)
        .build();

    Mockito.doReturn(Calls.response(new JsonObject()))
        .when(mockCdnService)
        .transformDebug("", "handle");

    Assert.assertNotNull(fileLink.imageTransform().debug());
  }

  @Test
  public void testDebugExternal() throws Exception {
    String url = "https://example.com/image.jpg";
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsService mockFsService = new FsService(null, mockCdnService, null, null);
    FilestackClient client = new FilestackClient("apiKey", null, mockFsService);

    Mockito.doReturn(Calls.response(new JsonObject()))
        .when(mockCdnService)
        .transformDebugExt("apiKey", "", url);

    Assert.assertNotNull(client.imageTransform(url).debug());
  }

  @Test
  public void testStoreHandle() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsService mockFsService = new FsService(null, mockCdnService, null, null);

    FileLink fileLink = new FileLink.Builder()
        .apiKey("apiKey")
        .handle("handle")
        .service(mockFsService)
        .build();

    String jsonString = "{'url': 'https://cdn.filestackcontent.com/handle'}";
    Gson gson = new Gson();
    StoreResponse storeResponse = gson.fromJson(jsonString, StoreResponse.class);

    Mockito.doReturn(Calls.response(storeResponse))
        .when(mockCdnService)
        .transformStore("store", "handle");

    Assert.assertNotNull(fileLink.imageTransform().store());
  }

  @Test
  public void testStoreExternal() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    FsService mockFsService = new FsService(null, mockCdnService, null, null);

    FilestackClient client = new FilestackClient("apiKey", null, mockFsService);

    String jsonString = "{'url': 'https://cdn.filestackcontent.com/handle'}";
    Gson gson = new Gson();
    StoreResponse storeResponse = gson.fromJson(jsonString, StoreResponse.class);

    String url = "https://example.com/image.jpg";

    Mockito.doReturn(Calls.response(storeResponse))
        .when(mockCdnService)
        .transformStoreExt("apiKey", "store", url);

    Assert.assertNotNull(client.imageTransform(url).store());
  }

  @Test(expected = NullPointerException.class)
  public void testAddNullTask() throws Exception {
    FileLink filelink = new FileLink("apiKey", "handle");
    ImageTransform transform = filelink.imageTransform();
    transform.addTask(null);
  }
}
