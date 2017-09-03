package com.filestack;

import com.filestack.transforms.ImageTransform;
import com.filestack.transforms.tasks.CropTask;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import java.io.File;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class TestTransforms {
  private static final String API_KEY = System.getenv("API_KEY");
  private static final String POLICY = System.getenv("POLICY");
  private static final String SIGNATURE = System.getenv("SIGNATURE");
  private static final Security SECURITY = Security.fromExisting(POLICY, SIGNATURE);

  private static ArrayList<String> handles = new ArrayList<>();
  private static ArrayList<File> files = new ArrayList<>();

  @Test
  public void testImageTransform() throws Exception {
    FilestackClient client = new FilestackClient(API_KEY, SECURITY);

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String origPath = loader.getResource("com/filestack/sample_image.jpg").getPath();
    File origFile = new File(origPath);

    FileLink fileLink = client.upload(origPath);
    handles.add(fileLink.getHandle());

    ImageTransform transform = fileLink.imageTransform();
    transform.addTask(new CropTask(0, 0, 500, 500));

    String cropPath = loader.getResource("com/filestack/sample_image_cropped.jpg").getPath();
    File cropFile = new File(cropPath);

    String correct = Files.asByteSource(cropFile).hash(Hashing.sha256()).toString();
    byte[] bytes = transform.getContent().bytes();
    String output = Hashing.sha256().hashBytes(bytes).toString();

    Assert.assertEquals(correct, output);
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
