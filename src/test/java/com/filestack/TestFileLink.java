package com.filestack;

import com.filestack.internal.BaseService;
import com.filestack.internal.CdnService;
import com.filestack.internal.Networking;
import com.google.common.io.Files;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

  @Before
  public void setup() {
    CdnService mockCdnService = Mockito.mock(CdnService.class);
    BaseService mockBaseService = Mockito.mock(BaseService.class);
    Networking.setCdnService(mockCdnService);
    Networking.setBaseService(mockBaseService);
  }

  @After
  public void teardown() {
    Networking.invalidate();
  }

  @Test
  public void testGetContent() throws Exception {
    Mockito
        .doReturn(Helpers.createRawCall("text/plain", "Test content"))
        .when(Networking.getCdnService())
        .get("handle", null, null);

    Config config = new Config("apikey");
    FileLink fileLink = new FileLink(config, "handle");

    ResponseBody content = fileLink.getContent();
    Assert.assertEquals("Test content", content.string());
  }

  @Test
  public void testDownload() throws Exception {
    Mockito
        .doReturn(Helpers.createRawCall("text/plain", "Test content"))
        .when(Networking.getCdnService())
        .get("handle", null, null);

    Config config = new Config("apikey");
    FileLink fileLink = new FileLink(config, "handle");

    File file = fileLink.download("/tmp/");
    Assert.assertTrue(file.isFile());
    if (!file.delete()) {
      Assert.fail("Unable to cleanup resource");
    }
  }

  @Test
  public void testDownloadCustomFilename() throws Exception {
    Mockito
        .doReturn(Helpers.createRawCall("text/plain", "Test content"))
        .when(Networking.getCdnService())
        .get("handle", null, null);

    Config config = new Config("apikey");
    FileLink fileLink = new FileLink(config, "handle");

    File file = fileLink.download("/tmp/", "filestack_test_filelink_download.txt");
    Assert.assertTrue(file.isFile());
    if (!file.delete()) {
      Assert.fail("Unable to cleanup resource");
    }
  }

  @Test
  public void testOverwrite() throws Exception {
    String pathname = "/tmp/filestack_test_filelink_overwrite.txt";
    File file = new File(pathname);
    if (!file.createNewFile()) {
      Assert.fail("Unable to create resource");
    }
    Files.write("Test content".getBytes(), file);

    Mockito
        .doReturn(Helpers.createRawCall("application/json", ""))
        .when(Networking.getBaseService())
        .overwrite(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.any(RequestBody.class));

    Config config = new Config("apiKey", "policy", "signature");
    FileLink fileLink = new FileLink(config, "handle");

    fileLink.overwrite(pathname);

    if (!file.delete()) {
      Assert.fail("Unable to cleanup resource");
    }
  }

  @Test
  public void testDelete() throws Exception {
    Mockito
        .doReturn(Helpers.createRawCall("application/json", ""))
        .when(Networking.getBaseService())
        .delete("handle", "apiKey", "policy", "signature");

    Config config = new Config("apiKey", "policy", "signature");
    FileLink fileLink = new FileLink(config, "handle");

    fileLink.delete();
  }

  @Test(expected = IllegalStateException.class)
  public void testOverwriteWithoutSecurity() throws Exception {
    Config config = new Config("apiKey");
    FileLink fileLink = new FileLink(config, "handle");
    fileLink.overwrite("");
  }

  @Test(expected = FileNotFoundException.class)
  public void testOverwriteNoFile() throws Exception {
    Config config = new Config("apiKey", "policy", "signature");
    FileLink fileLink = new FileLink(config, "handle");
    fileLink.overwrite("/tmp/filestack_test_overwrite_no_file.txt");
  }

  @Test(expected = IllegalStateException.class)
  public void testDeleteWithoutSecurity() throws Exception {
    Config config = new Config("apiKey");
    FileLink fileLink = new FileLink(config, "handle");
    fileLink.delete();
  }

  @Test(expected = IllegalStateException.class)
  public void testImageTagNoSecurity() throws Exception {
    Config config = new Config("apiKey");
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

    String tasksString = "security=policy:policy,signature:signature/tags";

    Mockito
        .doReturn(Helpers.createRawCall("application/json", jsonString))
        .when(Networking.getCdnService())
        .transform(tasksString, "handle");

    Config config = new Config("apiKey", "policy", "signature");
    FileLink fileLink = new FileLink(config, "handle");

    Map<String, Integer> tags = fileLink.imageTags();

    Assert.assertEquals((Integer) 100, tags.get("giraffe"));
  }

  @Test(expected = IllegalStateException.class)
  public void testImageSfwNoSecurity() throws Exception {
    Config config = new Config("apiKey");
    FileLink fileLink = new FileLink(config, "handle");
    fileLink.imageSfw();
  }

  @Test
  public void testImageSfw() throws Exception {
    Mockito.doAnswer(new Answer() {
      @Override
      public Call<ResponseBody> answer(InvocationOnMock invocation) throws Throwable {
        String handle = invocation.getArgument(1);
        String json = "{'sfw': " + (handle.equals("safe") ? "true" : "false") + "}";
        MediaType mediaType = MediaType.parse("application/json");
        return Calls.response(ResponseBody.create(mediaType, json));
      }
    })
        .when(Networking.getCdnService())
        .transform(Mockito.anyString(), Mockito.anyString());

    Config config = new Config("apiKey", "policy", "signature");

    FileLink safe = new FileLink(config, "safe");
    FileLink notSafe = new FileLink(config, "notSafe");

    Assert.assertTrue(safe.imageSfw());
    Assert.assertFalse(notSafe.imageSfw());
  }
}
