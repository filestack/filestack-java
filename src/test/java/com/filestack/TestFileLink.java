package org.filestack;

import org.filestack.internal.BaseService;
import org.filestack.internal.CdnService;
import org.filestack.internal.MockResponse;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import static org.filestack.internal.MockResponse.success;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link FileLink FileLink} class.
 */
public class TestFileLink {

  private CdnService cdnService = mock(CdnService.class);
  private BaseService baseService = mock(BaseService.class);

  @Test
  public void testGetContent() throws Exception {
    ResponseBody responseBody = ResponseBody.create(
        MediaType.get("text/plain"),
        "Test content"
    );

    when(cdnService.get("handle", null, null))
        .thenReturn(MockResponse.<ResponseBody>success(responseBody));

    Config config = new Config("apikey");
    FileLink fileLink = new FileLink(config, cdnService, baseService, "handle");

    ResponseBody content = fileLink.getContent();
    Assert.assertEquals("Test content", content.string());
  }

  @Test
  public void testDownload() throws IOException {
    ResponseBody responseBody = ResponseBody.create(
        MediaType.get("text/plain"),
        "Test content"
    );

    when(cdnService.get("handle", null, null))
        .thenReturn(MockResponse.<ResponseBody>success(responseBody));

    Config config = new Config("apikey");
    FileLink fileLink = new FileLink(config, cdnService, baseService, "handle");

    try {
      File file = fileLink.download("/tmp/");
      Assert.assertTrue(file.isFile());
      if (!file.delete()) {
        Assert.fail("Unable to cleanup resource");
      }
    } catch (IOException e) {
      e.printStackTrace(); // Example: print stack trace
    }
  }

  @Test
  public void testDownloadCustomFilename() throws Exception {
    ResponseBody responseBody = ResponseBody.create(
        MediaType.get("text/plain"),
        "Test content"
    );

    when(cdnService.get("handle", null, null))
        .thenReturn(MockResponse.<ResponseBody>success(responseBody));

    Config config = new Config("apikey");
    FileLink fileLink = new FileLink(config, cdnService, baseService, "handle");
    try {
      File file = fileLink.download("/tmp/", "filestack_test_filelink_download.txt");
      Assert.assertTrue(file.isFile());
      if (!file.delete()) {
        Assert.fail("Unable to cleanup resource");
      }
    } catch (IOException e) {
      e.printStackTrace(); // Example: print stack trace
    }
  }

  @Test(expected = IllegalStateException.class)
  public void testOverwriteWithoutSecurity() throws Exception {
    Config config = new Config("apiKey");
    FileLink fileLink = new FileLink(config, cdnService, baseService, "handle");
    fileLink.overwrite("");
  }

  @Test(expected = FileNotFoundException.class)
  public void testOverwriteNoFile() throws Exception {
    Config config = new Config("apiKey", "policy", "signature");
    FileLink fileLink = new FileLink(config, cdnService, baseService, "handle");
    fileLink.overwrite("/tmp/filestack_test_overwrite_no_file.txt");
  }

  @Test(expected = IllegalStateException.class)
  public void testDeleteWithoutSecurity() throws Exception {
    Config config = new Config("apiKey");
    FileLink fileLink = new FileLink(config, cdnService, baseService, "handle");
    fileLink.delete();
  }

  @Test(expected = IllegalStateException.class)
  public void testImageTagNoSecurity() throws Exception {
    Config config = new Config("apiKey");
    FileLink fileLink = new FileLink(config, cdnService, baseService, "handle");
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

    ResponseBody responseBody = ResponseBody.create(
        MediaType.get("application/json"),
        jsonString
    );

    when(cdnService.transform(tasksString, "handle"))
        .thenReturn(success(responseBody));

    Config config = new Config("apiKey", "policy", "signature");
    FileLink fileLink = new FileLink(config, cdnService, baseService, "handle");

    Map<String, Integer> tags = fileLink.imageTags();

    Assert.assertEquals((Integer) 100, tags.get("giraffe"));
  }

  @Test(expected = IllegalStateException.class)
  public void testImageSfwNoSecurity() throws Exception {
    Config config = new Config("apiKey");
    FileLink fileLink = new FileLink(config, cdnService, baseService, "handle");
    fileLink.imageSfw();
  }

  @Test
  public void testImageSfw() throws Exception {
    ResponseBody safeBody = ResponseBody.create(
        MediaType.get("application/json"),
        "{ \"sfw\": true }"
    );

    ResponseBody notSafeBody = ResponseBody.create(
        MediaType.get("application/json"),
        "{ \"sfw\": false }"
    );

    when(cdnService.transform(anyString(), eq("safe")))
        .thenReturn(MockResponse.<ResponseBody>success(safeBody));

    when(cdnService.transform(anyString(), eq("notSafe")))
        .thenReturn(MockResponse.<ResponseBody>success(notSafeBody));


    Config config = new Config("apiKey", "policy", "signature");

    FileLink safe = new FileLink(config, cdnService, baseService, "safe");
    FileLink notSafe = new FileLink(config, cdnService, baseService, "notSafe");

    Assert.assertTrue(safe.imageSfw());
    Assert.assertFalse(notSafe.imageSfw());
  }
}
