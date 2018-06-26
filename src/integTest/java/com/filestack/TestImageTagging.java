package com.filestack;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class TestImageTagging {
  private static final String API_KEY = System.getenv("API_KEY");
  private static final String POLICY = System.getenv("POLICY");
  private static final String SIGNATURE = System.getenv("SIGNATURE");

  private static final Config config = new Config(API_KEY, POLICY, SIGNATURE);
  private static final Client client = new Client(config);

  private static ArrayList<String> HANDLES = new ArrayList<>();
  private static ArrayList<File> FILES = new ArrayList<>();

  /**
   * TODO Possibly modify these to be reliable
   * These tests have intermittently failed so many times they're useless. The aim isn't to test and deal with backend
   * issues, but every failure has been for a backend or account issue. Disabling them for now.
   */

  // @Test
  public void testImageTags() throws Exception {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String origPath = loader.getResource("com/filestack/sample_image.jpg").getPath();

    StorageOptions storeOpts = new StorageOptions.Builder()
        .filename("nebula.jpg")
        .mimeType("image/jpeg")
        .build();
    FileLink fileLink = client.upload(origPath, true, storeOpts);
    HANDLES.add(fileLink.getHandle());

    Map<String, Integer> tags = fileLink.imageTags();
    Assert.assertTrue(tags.containsKey("nebula"));
  }

  /** TODO This shouldn't expect certain tags returned as the results vary. */
  // @Test
  public void testImageSfw() throws Exception {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String origPath = loader.getResource("com/filestack/sample_image.jpg").getPath();

    StorageOptions storeOpts = new StorageOptions.Builder()
        .filename("nebula.jpg")
        .mimeType("image/jpeg")
        .build();
    FileLink fileLink = client.upload(origPath, false, storeOpts);
    HANDLES.add(fileLink.getHandle());

    Assert.assertTrue(fileLink.imageSfw());
  }

  /** Deletes any FILES uploaded during tests. */
  @AfterClass
  public static void cleanupHandles() {
    for (String handle : HANDLES) {
      FileLink fileLink = new FileLink(config, handle);
      try {
        fileLink.delete();
      } catch (Exception e) {
        Assert.fail("FileLink delete failed");
      }
    }
  }

  /** Deletes any local FILES created during tests. */
  @AfterClass
  public static void cleanupFiles() {
    for (File file : FILES) {
      if (!file.delete()) {
        Assert.fail("Unable to cleanup resource");
      }
    }
  }
}