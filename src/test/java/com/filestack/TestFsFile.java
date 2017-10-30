package com.filestack;

import com.filestack.util.FsApiService;
import com.filestack.util.FsCdnService;
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
 * Tests {@link FsFile FsFile} class.
 */
public class TestFsFile {
  @Test
  public void testConstructors() {
    FsConfig config = new FsConfig.Builder().apiKey("apiKey").build();
    FsFile fsFile = new FsFile(config, "handle");
  }

  @Test
  public void testGetContent() throws Exception {
    MediaType mediaType = MediaType.parse("text/plain");
    ResponseBody response = ResponseBody.create(mediaType, "Test content");
    Call call = Calls.response(response);

    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    Mockito.doReturn(call).when(mockCdnService).get("handle", null, null);

    FsConfig config = new FsConfig.Builder()
        .apiKey("apiKey")
        .cdnService(mockCdnService)
        .build();
    FsFile fsFile = new FsFile(config, "handle");

    ResponseBody content = fsFile.getContent();
    Assert.assertEquals("Test content", content.string());
  }

  @Test
  public void testDownload() throws Exception {
    MediaType mediaType = MediaType.parse("text/plain");
    ResponseBody response = ResponseBody.create(mediaType, "Test content");
    Call call = Calls.response(response);

    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    Mockito.doReturn(call)
        .when(mockCdnService)
        .get("handle", null, null);

    FsConfig config = new FsConfig.Builder()
        .apiKey("apiKey")
        .cdnService(mockCdnService)
        .build();
    FsFile fsFile = new FsFile(config, "handle");

    File file = fsFile.download("/tmp/");
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

    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    Mockito.doReturn(call).when(mockCdnService).get("handle", null, null);

    FsConfig config = new FsConfig.Builder()
        .apiKey("apiKey")
        .cdnService(mockCdnService)
        .build();
    FsFile fsFile = new FsFile(config, "handle");

    File file = fsFile.download("/tmp/", "filestack_test_filelink_download.txt");
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

    FsApiService mockApiService = Mockito.mock(FsApiService.class);
    Mockito.doReturn(call)
        .when(mockApiService)
        .overwrite(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.any(RequestBody.class));

    FsConfig config = new FsConfig.Builder()
        .apiKey("apiKey")
        .security("policy", "signature")
        .apiService(mockApiService)
        .build();
    FsFile fsFile = new FsFile(config, "handle");

    fsFile.overwrite(pathname);

    if (!file.delete()) {
      Assert.fail("Unable to cleanup resource");
    }
  }

  @Test
  public void testDelete() throws Exception {
    MediaType mediaType = MediaType.parse("application/json");
    ResponseBody response = ResponseBody.create(mediaType, "");
    Call call = Calls.response(response);

    FsApiService mockApiService = Mockito.mock(FsApiService.class);
    Mockito.doReturn(call)
        .when(mockApiService)
        .delete("handle", "apiKey", "policy", "signature");

    FsConfig config = new FsConfig.Builder()
        .apiKey("apiKey")
        .security("policy", "signature")
        .apiService(mockApiService)
        .build();
    FsFile fsFile = new FsFile(config, "handle");

    fsFile.delete();
  }

  @Test(expected = IllegalStateException.class)
  public void testOverwriteWithoutSecurity() throws Exception {
    FsConfig config = new FsConfig.Builder().apiKey("apiKey").build();
    FsFile fsFile = new FsFile(config, "handle");
    fsFile.overwrite("");
  }

  @Test(expected = FileNotFoundException.class)
  public void testOverwriteNoFile() throws Exception {
    FsConfig config = new FsConfig.Builder()
        .apiKey("apiKey")
        .security("policy", "signature")
        .build();
    FsFile fsFile = new FsFile(config, "handle");

    fsFile.overwrite("/tmp/filestack_test_overwrite_no_file.txt");
  }

  @Test(expected = IllegalStateException.class)
  public void testDeleteWithoutSecurity() throws Exception {
    FsConfig config = new FsConfig.Builder().apiKey("apiKey").build();
    FsFile fsFile = new FsFile(config, "handle");
    fsFile.delete();
  }

  @Test(expected = IllegalStateException.class)
  public void testImageTagNoSecurity() throws Exception {
    FsConfig config = new FsConfig.Builder().apiKey("apiKey").build();
    FsFile fsFile = new FsFile(config, "handle");
    fsFile.imageTags();
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

    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
    Mockito.doReturn(call)
        .when(mockCdnService)
        .transform(tasksString, "handle");

    FsConfig config = new FsConfig.Builder()
        .apiKey("apiKey")
        .security("policy", "signature")
        .cdnService(mockCdnService)
        .build();
    FsFile fsFile = new FsFile(config, "handle");

    Map<String, Integer> tags = fsFile.imageTags();

    Assert.assertEquals((Integer) 100, tags.get("giraffe"));
  }

  @Test(expected = IllegalStateException.class)
  public void testImageSfwNoSecurity() throws Exception {
    FsConfig config = new FsConfig.Builder().apiKey("apiKey").build();
    FsFile fsFile = new FsFile(config, "handle");
    fsFile.imageSfw();
  }

  @Test
  public void testImageSfw() throws Exception {
    FsCdnService mockCdnService = Mockito.mock(FsCdnService.class);
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

    FsConfig config = new FsConfig.Builder()
        .apiKey("apiKey")
        .security("policy", "signature")
        .cdnService(mockCdnService)
        .build();

    FsFile safe = new FsFile(config, "safe");
    FsFile notSafe = new FsFile(config, "notSafe");

    Assert.assertTrue(safe.imageSfw());
    Assert.assertFalse(notSafe.imageSfw());
  }
}
