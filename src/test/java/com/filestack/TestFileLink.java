package com.filestack;

import com.filestack.errors.FilestackException;
import com.filestack.errors.ValidationException;
import com.filestack.util.FsService;
import com.google.common.io.Files;
import java.io.File;
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

/**
 * Tests {@link FileLink FileLink} class.
 */
public class TestFileLink {

  @Test
  public void testConstructors() {
    Policy policy = new Policy.Builder().giveFullAccess().build();
    Security security = Security.createNew(policy, "app_secret");

    FileLink fileLink1 = new FileLink("apiKey", "handle");
    FileLink fileLink2 = new FileLink("apiKey", "handle", security);
  }

  @Test
  public void testGetContent() throws Exception {
    FsService mockFsService = Mockito.mock(FsService.class);

    MediaType mediaType = MediaType.parse("text/plain");
    ResponseBody response = ResponseBody.create(mediaType, "Test content");
    Call call = Calls.response(response);

    Mockito.doReturn(call)
        .when(mockFsService)
        .get("handle", null, null);

    FileLink fileLink = new FileLink.Builder()
        .apiKey("apiKey")
        .handle("handle")
        .service(mockFsService)
        .build();

    byte[] content = fileLink.getContent();
    String text = new String(content);
    Assert.assertEquals("Test content", text);
  }

  @Test
  public void testDownload() throws Exception {
    FsService mockFsService = Mockito.mock(FsService.class);

    MediaType mediaType = MediaType.parse("text/plain");
    ResponseBody response = ResponseBody.create(mediaType, "Test content");
    Call call = Calls.response(response);

    Mockito.doReturn(call)
        .when(mockFsService)
        .get("handle", null, null);

    FileLink fileLink = new FileLink.Builder()
        .apiKey("apiKey")
        .handle("handle")
        .service(mockFsService)
        .build();

    File file = fileLink.download("/tmp/");
    Assert.assertTrue(file.isFile());
    if (!file.delete()) {
      Assert.fail("Unable to cleanup resource");
    }
  }

  @Test
  public void testDownloadCustomFilename() throws Exception {
    FsService mockFsService = Mockito.mock(FsService.class);

    MediaType mediaType = MediaType.parse("text/plain");
    ResponseBody response = ResponseBody.create(mediaType, "Test content");
    Call call = Calls.response(response);

    Mockito.doReturn(call)
        .when(mockFsService)
        .get("handle", null, null);

    FileLink fileLink = new FileLink.Builder()
        .apiKey("apiKey")
        .handle("handle")
        .service(mockFsService)
        .build();

    File file = fileLink.download("/tmp/", "filestack_test_filelink_download.txt");
    Assert.assertTrue(file.isFile());
    if (!file.delete()) {
      Assert.fail("Unable to cleanup resource");
    }
  }

  @Test
  public void testOverwrite() throws Exception {
    FsService mockFsService = Mockito.mock(FsService.class);

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

    Mockito.doReturn(call)
        .when(mockFsService)
        .overwrite(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.any(RequestBody.class));

    FileLink fileLink = new FileLink.Builder()
        .apiKey("apiKey")
        .handle("handle")
        .security(security)
        .service(mockFsService)
        .build();

    fileLink.overwrite(pathname);

    if (!file.delete()) {
      Assert.fail("Unable to cleanup resource");
    }
  }

  @Test
  public void testDelete() throws Exception {
    FsService mockFsService = Mockito.mock(FsService.class);

    MediaType mediaType = MediaType.parse("application/json");
    ResponseBody response = ResponseBody.create(mediaType, "");
    Call call = Calls.response(response);

    Policy policy = new Policy.Builder().giveFullAccess().build();
    Security security = Security.createNew(policy, "appSecret");

    Mockito.doReturn(call)
        .when(mockFsService)
        .delete("handle", "apiKey", security.getPolicy(), security.getSignature());

    FileLink fileLink = new FileLink.Builder()
        .apiKey("apiKey")
        .handle("handle")
        .security(security)
        .service(mockFsService)
        .build();

    fileLink.delete();
  }

  @Test(expected = FilestackException.class)
  public void testOverwriteWithoutSecurity() throws Exception {
    FileLink fileLink = new FileLink("apiKey", "handle");
    fileLink.overwrite("");
  }

  @Test(expected = ValidationException.class)
  public void testOverwriteNoFile() throws Exception {
    Policy policy = new Policy.Builder().giveFullAccess().build();
    Security security = Security.createNew(policy, "appSecret");
    FileLink fileLink = new FileLink("apiKey", "handle", security);

    fileLink.overwrite("/tmp/filestack_test_overwrite_no_file.txt");
  }

  @Test(expected = FilestackException.class)
  public void testDeleteWithoutSecurity() throws Exception {
    FileLink fileLink = new FileLink("apiKey", "handle");
    fileLink.delete();
  }

  @Test(expected = ValidationException.class)
  public void testImageTagNoSecurity() throws Exception {
    FileLink fileLink = new FileLink("apiKey", "handle");
    fileLink.imageTag();
  }

  @Test
  public void testImageTag() throws Exception {
    FsService mockFsService = Mockito.mock(FsService.class);

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

    Policy policy = new Policy.Builder().giveFullAccess().build();
    Security security = Security.createNew(policy, "appSecret");

    String tasksString = "security=policy:" + security.getPolicy()
        + ",signature:" + security.getSignature()
        + "/tags";

    Mockito.doReturn(call)
        .when(mockFsService)
        .transform(tasksString, "handle");

    FileLink fileLink = new FileLink.Builder()
        .apiKey("apiKey")
        .handle("handle")
        .security(security)
        .service(mockFsService)
        .build();

    ImageTags imageTags = fileLink.imageTag();

    Assert.assertEquals((Integer) 100, imageTags.getAuto().get("giraffe"));
  }

  @Test(expected = ValidationException.class)
  public void testImageSfwNoSecurity() throws Exception {
    FileLink fileLink = new FileLink("apiKey", "handle");
    fileLink.imageSfw();
  }

  @Test
  public void testImageSfw() throws Exception {
    FsService mockFsService = Mockito.mock(FsService.class);

    Mockito.doAnswer(new Answer() {
      @Override
      public Call<ResponseBody> answer(InvocationOnMock invocation) throws Throwable {
        String handle = invocation.getArgument(1);
        String json = "{'sfw': " + (handle.equals("safe") ? "true" : "false") + "}";
        MediaType mediaType = MediaType.parse("application/json");
        return Calls.response(ResponseBody.create(mediaType, json));
      }
    })
        .when(mockFsService)
        .transform(Mockito.anyString(), Mockito.anyString());

    Policy policy = new Policy.Builder().giveFullAccess().build();
    Security security = Security.createNew(policy, "appSecret");

    FileLink.Builder builder = new FileLink.Builder()
        .apiKey("apiKey")
        .security(security)
        .service(mockFsService);

    FileLink safe = builder.handle("safe").build();
    FileLink notSafe = builder.handle("notSafe").build();

    Assert.assertTrue(safe.imageSfw());
    Assert.assertFalse(notSafe.imageSfw());
  }
}
