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
    FsClient client = new FsClient(API_KEY, SECURITY);

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String origPath = loader.getResource("com/filestack/sample_image.jpg").getPath();
    File origFile = new File(origPath);

    FsFile fsFile = client.upload(origPath, true);
    handles.add(fsFile.getHandle());

    Map<String, Integer> tags = fsFile.imageTags();
    Assert.assertNotNull(tags.get("nebula"));
  }

  @Test
  public void testImageSfw() throws Exception {
    FsClient client = new FsClient(API_KEY, SECURITY);

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String origPath = loader.getResource("com/filestack/sample_image.jpg").getPath();
    File origFile = new File(origPath);

    FsFile fsFile = client.upload(origPath, false);
    handles.add(fsFile.getHandle());

    Assert.assertTrue(fsFile.imageSfw());
  }

  /** Deletes any files uploaded during tests. */
  @AfterClass
  public static void cleanupHandles() {
    for (String handle : handles) {
      FsFile fsFile = new FsFile(API_KEY, handle, SECURITY);
      try {
        fsFile.delete();
      } catch (Exception e) {
        Assert.fail("FsFile delete failed");
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