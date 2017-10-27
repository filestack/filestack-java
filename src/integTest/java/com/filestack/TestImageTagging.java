package com.filestack;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class TestImageTagging {
  private static final FsConfig config = new FsConfig.Builder()
      .apiKey(System.getenv("API_KEY"))
      .security(System.getenv("POLICY"), System.getenv("SIGNATURE"))
      .build();
  private static final FsClient client = new FsClient(config);

  private static ArrayList<String> HANDLES = new ArrayList<>();
  private static ArrayList<File> FILES = new ArrayList<>();

  @Test
  public void testImageTags() throws Exception {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String origPath = loader.getResource("com/filestack/sample_image.jpg").getPath();
    File origFile = new File(origPath);

    FsFile fsFile = client.upload(origPath, true);
    HANDLES.add(fsFile.getHandle());

    Map<String, Integer> tags = fsFile.imageTags();
    Assert.assertNotNull(tags.get("nebula"));
  }

  @Test
  public void testImageSfw() throws Exception {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String origPath = loader.getResource("com/filestack/sample_image.jpg").getPath();
    File origFile = new File(origPath);

    FsFile fsFile = client.upload(origPath, false);
    HANDLES.add(fsFile.getHandle());

    Assert.assertTrue(fsFile.imageSfw());
  }

  /** Deletes any FILES uploaded during tests. */
  @AfterClass
  public static void cleanupHandles() {
    for (String handle : HANDLES) {
      FsFile fsFile = new FsFile(config, handle);
      try {
        fsFile.delete();
      } catch (Exception e) {
        Assert.fail("FsFile delete failed");
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