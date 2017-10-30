package com.filestack;

import com.filestack.internal.BaseService;
import com.filestack.internal.CdnService;
import com.google.common.io.Files;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit2.Call;
import retrofit2.mock.Calls;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Tests {@link FileLink FileLink} class.
 */
public class TestFileLink {
  @Test
  public void testConstructors() {
    Config config = new Config.Builder().apiKey("apiKey").build();
    FileLink fileLink = new FileLink(config, "handle");
  }

  @Test
  public void testGetContent() throws Exception {
    MediaType mediaType = MediaType.parse("text/plain");
    ResponseBody response = ResponseBody.create(mediaType, "Test content");
    Call call = Calls.response(response);

    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Mockito.doReturn(call).when(mockCdnService).get("handle", null, null);

    Config config = new Config.Builder()
        .apiKey("apiKey")
        .cdnService(mockCdnService)
        .build();
    FileLink fileLink = new FileLink(config, "handle");

    ResponseBody content = fileLink.getContent();
    Assert.assertEquals("Test content", content.string());
  }

  @Test
  public void testDownload() throws Exception {
    MediaType mediaType = MediaType.parse("text/plain");
    ResponseBody response = ResponseBody.create(mediaType, "Test content");
    Call call = Calls.response(response);

    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Mockito.doReturn(call)
        .when(mockCdnService)
        .get("handle", null, null);

    Config config = new Config.Builder()
        .apiKey("apiKey")
        .cdnService(mockCdnService)
        .build();
    FileLink fileLink = new FileLink(config, "handle");

    File file = fileLink.download("/tmp/");
    Assert.assertTrue(file.isFile());
    if (!file.delete()) {
      Assert.fail("Unable to cleanup resource");
    }
  }

  @Test
  public void testDownloadCustomFilename() throws Exception {
    MediaType mediaType = MediaType.parse("text/plain");
    ResponseBody response = ResponseBody.create(mediaType, "Test content");
    Call call = Calls.response(response);

    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Mockito.doReturn(call).when(mockCdnService).get("handle", null, null);

    Config config = new Config.Builder()
        .apiKey("apiKey")
        .cdnService(mockCdnService)
        .build();
    FileLink fileLink = new FileLink(config, "handle");

    File file = fileLink.download("/tmp/", "filestack_test_filelink_download.txt");
    Assert.assertTrue(file.isFile());
    if (!file.delete()) {
      Assert.fail("Unable to cleanup resource");
    }
  }

  @Test
  public void testOverwrite() throws Exception {
    MediaType jsonType = MediaType.parse("application/json");
    ResponseBody response = ResponseBody.create(jsonType, "");
    Call call = Calls.response(response);

    String pathname = "/tmp/filestack_test_filelink_overwrite.txt";
    File file = new File(pathname);
    if (!file.createNewFile()) {
      Assert.fail("Unable to create resource");
    }
    Files.write("Test content".getBytes(), file);

    MediaType textType = MediaType.parse("text/plain");
    RequestBody body = RequestBody.create(textType, file);

    Policy policy = new Policy.Builder().giveFullAccess().build();
    Security security = Security.createNew(policy, "appSecret");

    BaseService mockApiService = Mockito.mock(BaseService.class);
    Mockito.doReturn(call)
        .when(mockApiService)
        .overwrite(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.any(RequestBody.class));

    Config config = new Config.Builder()
        .apiKey("apiKey")
        .security("policy", "signature")
        .apiService(mockApiService)
        .build();
    FileLink fileLink = new FileLink(config, "handle");

    fileLink.overwrite(pathname);

    if (!file.delete()) {
      Assert.fail("Unable to cleanup resource");
    }
  }

  @Test
  public void testDelete() throws Exception {
    MediaType mediaType = MediaType.parse("application/json");
    ResponseBody response = ResponseBody.create(mediaType, "");
    Call call = Calls.response(response);

    BaseService mockApiService = Mockito.mock(BaseService.class);
    Mockito.doReturn(call)
        .when(mockApiService)
        .delete("handle", "apiKey", "policy", "signature");

    Config config = new Config.Builder()
        .apiKey("apiKey")
        .security("policy", "signature")
        .apiService(mockApiService)
        .build();
    FileLink fileLink = new FileLink(config, "handle");

    fileLink.delete();
  }

  @Test(expected = IllegalStateException.class)
  public void testOverwriteWithoutSecurity() throws Exception {
    Config config = new Config.Builder().apiKey("apiKey").build();
    FileLink fileLink = new FileLink(config, "handle");
    fileLink.overwrite("");
  }

  @Test(expected = FileNotFoundException.class)
  public void testOverwriteNoFile() throws Exception {
    Config config = new Config.Builder()
        .apiKey("apiKey")
        .security("policy", "signature")
        .build();
    FileLink fileLink = new FileLink(config, "handle");

    fileLink.overwrite("/tmp/filestack_test_overwrite_no_file.txt");
  }

  @Test(expected = IllegalStateException.class)
  public void testDeleteWithoutSecurity() throws Exception {
    Config config = new Config.Builder().apiKey("apiKey").build();
    FileLink fileLink = new FileLink(config, "handle");
    fileLink.delete();
  }

  @Test(expected = IllegalStateException.class)
  public void testImageTagNoSecurity() throws Exception {
    Config config = new Config.Builder().apiKey("apiKey").build();
    FileLink fileLink = new FileLink(config, "handle");
    fileLink.imageTags();
  }

  @Test
  public void testImageTag() throws Exception {
    String jsonString = "{"
        + "'tags': {"
        + "'auto': {"
        + "'giraffe': 100"
        + "},"
        + "'user': null"
        + "}"
        + "}";

    MediaType mediaType = MediaType.parse("application/json");
    ResponseBody responseBody = ResponseBody.create(mediaType, jsonString);
    Call call = Calls.response(responseBody);

    String tasksString = "security=policy:policy,signature:signature/tags";

    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Mockito.doReturn(call)
        .when(mockCdnService)
        .transform(tasksString, "handle");

    Config config = new Config.Builder()
        .apiKey("apiKey")
        .security("policy", "signature")
        .cdnService(mockCdnService)
        .build();
    FileLink fileLink = new FileLink(config, "handle");

    Map<String, Integer> tags = fileLink.imageTags();

    Assert.assertEquals((Integer) 100, tags.get("giraffe"));
  }

  @Test(expected = IllegalStateException.class)
  public void testImageSfwNoSecurity() throws Exception {
    Config config = new Config.Builder().apiKey("apiKey").build();
    FileLink fileLink = new FileLink(config, "handle");
    fileLink.imageSfw();
  }

  @Test
  public void testImageSfw() throws Exception {
    CdnService mockCdnService = Mockito.mock(CdnService.class);
    Mockito.doAnswer(new Answer() {
      @Override
      public Call<ResponseBody> answer(InvocationOnMock invocation) throws Throwable {
        String handle = invocation.getArgument(1);
        String json = "{'sfw': " + (handle.equals("safe") ? "true" : "false") + "}";
        MediaType mediaType = MediaType.parse("application/json");
        return Calls.response(ResponseBody.create(mediaType, json));
      }
    })
        .when(mockCdnService)
        .transform(Mockito.anyString(), Mockito.anyString());

    Config config = new Config.Builder()
        .apiKey("apiKey")
        .security("policy", "signature")
        .cdnService(mockCdnService)
        .build();

    FileLink safe = new FileLink(config, "safe");
    FileLink notSafe = new FileLink(config, "notSafe");

    Assert.assertTrue(safe.imageSfw());
    Assert.assertFalse(notSafe.imageSfw());
  }
}
