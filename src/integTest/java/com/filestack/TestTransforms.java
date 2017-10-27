package com.filestack;

import com.filestack.transforms.AvTransform;
import com.filestack.transforms.ImageTransform;
import com.filestack.transforms.tasks.AvTransformOptions;
import com.filestack.transforms.tasks.CropTask;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

public class TestTransforms {
  private static final FsConfig config = new FsConfig.Builder()
      .apiKey(System.getenv("API_KEY"))
      .security(System.getenv("POLICY"), System.getenv("SIGNATURE"))
      .build();
  private static final FsClient client = new FsClient(config);

  private static ArrayList<String> HANDLES = new ArrayList<>();
  private static ArrayList<File> FILES = new ArrayList<>();

  @Test
  public void testImageTransform() throws Exception {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String origPath = loader.getResource("com/filestack/sample_image.jpg").getPath();
    File origFile = new File(origPath);

    FsFile fsFile = client.upload(origPath, false);
    HANDLES.add(fsFile.getHandle());

    ImageTransform transform = fsFile.imageTransform();
    transform.addTask(new CropTask(0, 0, 500, 500));

    String cropPath = loader.getResource("com/filestack/sample_image_cropped.jpg").getPath();
    File cropFile = new File(cropPath);

    String correct = Files.asByteSource(cropFile).hash(Hashing.sha256()).toString();
    byte[] bytes = transform.getContent().bytes();
    String output = Hashing.sha256().hashBytes(bytes).toString();

    Assert.assertEquals(correct, output);
  }

  @Test
  public void testAvTransform() throws Exception {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String oggPath = loader.getResource("com/filestack/sample_music.ogg").getPath();
    File oggFile = new File(oggPath);

    FsFile oggFsFile = client.upload(oggPath, false);
    HANDLES.add(oggFsFile.getHandle());

    AvTransformOptions options = new AvTransformOptions.Builder()
        .preset("mp3")
        .build();

    AvTransform transform = oggFsFile.avTransform(options);

    FsFile mp3FsFile;
    while ((mp3FsFile = transform.getFileLink()) == null) {
      Thread.sleep(5 * 1000);
    }
    HANDLES.add(mp3FsFile.getHandle());

    String mp3Path = loader.getResource("com/filestack/sample_music.mp3").getPath();
    File mp3File = new File(mp3Path);

    String correct = Files.asByteSource(mp3File).hash(Hashing.sha256()).toString();
    byte[] bytes = mp3FsFile.getContent().bytes();
    String output = Hashing.sha256().hashBytes(bytes).toString();

    Assert.assertEquals(correct, output);
  }

  /** Deletes any files uploaded during tests. */
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

  /** Deletes any local files created during tests. */
  @AfterClass
  public static void cleanupFiles() {
    for (File file : FILES) {
      if (!file.delete()) {
        Assert.fail("Unable to cleanup resource");
      }
    }
  }
}