package com.filestack;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class TestImageTagging {
  private static final String API_KEY = System.getenv("API_KEY");
  private static final String POLICY = System.getenv("POLICY");
  private static final String SIGNATURE = System.getenv("SIGNATURE");
  private static final Security SECURITY = Security.fromExisting(POLICY, SIGNATURE);

  private static ArrayList<String> handles = new ArrayList<>();
  private static ArrayList<File> files = new ArrayList<>();

  @Test
  public void testImageTags() throws Exception {
    FilestackClient client = new FilestackClient(API_KEY, SECURITY);

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String origPath = loader.getResource("com/filestack/sample_image.jpg").getPath();
    File origFile = new File(origPath);

    FileLink fileLink = client.upload(origPath, "image/jpeg");
    handles.add(fileLink.getHandle());

    Map<String, Integer> tags = fileLink.imageTags();
    Assert.assertNotNull(tags.get("nebula"));
  }

  @Test
  public void testImageSfw() throws Exception {
    FilestackClient client = new FilestackClient(API_KEY, SECURITY);

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String origPath = loader.getResource("com/filestack/sample_image.jpg").getPath();
    File origFile = new File(origPath);

    FileLink fileLink = client.upload(origPath, "image/jpeg");
    handles.add(fileLink.getHandle());

    Assert.assertTrue(fileLink.imageSfw());
  }

  /** Deletes any files uploaded during tests. */
  @AfterClass
  public static void cleanupHandles() {
    for (String handle : handles) {
      FileLink fileLink = new FileLink(API_KEY, handle, SECURITY);
      try {
        fileLink.delete();
      } catch (Exception e) {
        Assert.fail("FileLink delete failed");
      }
    }
  }

  /** Deletes any local files created during tests. */
  @AfterClass
  public static void cleanupFiles() {
    for (File file : files) {
      if (!file.delete()) {
        Assert.fail("Unable to cleanup resource");
      }
    }
  }
}