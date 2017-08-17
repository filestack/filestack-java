package com.filestack;

import static com.filestack.util.MockConstants.API_KEY;
import static com.filestack.util.MockConstants.HANDLE;
import static com.filestack.util.MockConstants.SECURITY;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.filestack.errors.FilestackException;
import com.filestack.errors.ValidationException;
import com.filestack.util.MockInterceptor;
import com.filestack.util.Networking;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileNotFoundException;

import okhttp3.OkHttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link FileLink FileLink} class.
 */
public class TestFileLink {
  private static final String DIRECTORY = "/tmp/";
  private static final String CUSTOM_FILENAME = "filestack_test_custom_filename.txt";
  private static final String OVERWRITE_PATHNAME = "/tmp/filestack_overwrite.txt";
  private static final String OVERWRITE_CONTENT = "Test overwrite content.";

  /**
   * Set a custom httpClient for our testing.
   * This custom client has an added interceptor to create mock responses.
   */
  @BeforeClass
  public static void setup() {
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new MockInterceptor())
        .build();
    Networking.setCustomClient(client);
  }

  @Test
  public void testInstantiation() {
    FileLink fileLink = new FileLink(API_KEY, HANDLE);
    assertNotNull("Unable to create FileLink", fileLink);
  }

  @Test
  public void testGetContent() throws Exception {
    FileLink fileLink = new FileLink(API_KEY, HANDLE);

    byte[] content = fileLink.getContent();
    String text = new String(content);
    assertTrue("Unexpected content in response", text.contains("Test content"));
  }

  @Test
  public void testGetContentWithSecurity() throws Exception {
    FileLink fileLink = new FileLink(API_KEY, HANDLE, SECURITY);

    byte[] content = fileLink.getContent();
    String text = new String(content);
    assertTrue("Unexpected content in response", text.contains("Test content"));
  }

  @Test
  public void testDownload() throws Exception {
    FileLink fileLink = new FileLink(API_KEY, HANDLE);

    File file = fileLink.download(DIRECTORY);
    assertTrue(file.isFile());
  }

  @Test
  public void testDownloadWithSecurity() throws Exception {
    FileLink fileLink = new FileLink(API_KEY, HANDLE, SECURITY);

    File file = fileLink.download(DIRECTORY);
    assertTrue(file.isFile());
  }

  @Test
  public void testDownloadCustomFilename() throws Exception {
    FileLink fileLink = new FileLink(API_KEY, HANDLE);
    File file = fileLink.download(DIRECTORY, CUSTOM_FILENAME);
    assertTrue(file.isFile());
  }

  @Test
  public void testOverwrite() throws Exception {
    FileLink fileLink = new FileLink(API_KEY, HANDLE, SECURITY);

    // Setup test file to read from
    File file = new File(OVERWRITE_PATHNAME);
    file.createNewFile();
    Files.write(OVERWRITE_CONTENT.getBytes(), file);

    fileLink.overwrite(OVERWRITE_PATHNAME);
  }

  @Test(expected = FilestackException.class)
  public void testOverwriteWithoutSecurity() throws Exception {
    FileLink fileLink = new FileLink(API_KEY, HANDLE);

    fileLink.overwrite(OVERWRITE_PATHNAME);
  }

  @Test(expected = ValidationException.class)
  public void testOverwriteNoFile() throws Exception {
    FileLink fileLink = new FileLink(API_KEY, HANDLE, SECURITY);

    File file = new File(OVERWRITE_PATHNAME);
    file.delete();

    fileLink.overwrite(OVERWRITE_PATHNAME);
  }

  @Test
  public void testDelete() throws Exception {
    FileLink fileLink = new FileLink(API_KEY, HANDLE, SECURITY);

    fileLink.delete();
  }

  @Test(expected = FilestackException.class)
  public void testDeleteWithoutSecurity() throws Exception {
    FileLink fileLink = new FileLink(API_KEY, HANDLE);

    fileLink.delete();
  }

  /**
   * Clear changes to {@link Networking Networking} class since it's a shared resource.
   */
  @AfterClass
  public static void teardown() {
    Networking.removeCustomClient();
  }
}
