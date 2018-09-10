package com.filestack;

import com.filestack.transforms.AvTransform;
import com.filestack.transforms.ImageTransform;
import com.filestack.transforms.tasks.AvTransformOptions;
import com.filestack.transforms.tasks.CropTask;
import okio.ByteString;
import okio.Okio;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class TestTransforms {
  private static final String API_KEY = System.getenv("API_KEY");
  private static final String POLICY = System.getenv("POLICY");
  private static final String SIGNATURE = System.getenv("SIGNATURE");

  private static final Config config = new Config(API_KEY, POLICY, SIGNATURE);
  private static final Client client = new Client(config);

  private static ArrayList<String> HANDLES = new ArrayList<>();
  private static ArrayList<File> FILES = new ArrayList<>();

  @Test
  public void testImageTransform() throws Exception {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String origPath = loader.getResource("com/filestack/sample_image.jpg").getPath();
    File origFile = new File(origPath);

    FileLink fileLink = client.upload(origPath, false);
    HANDLES.add(fileLink.getHandle());

    ImageTransform transform = fileLink.imageTransform();
    transform.addTask(new CropTask(0, 0, 500, 500));

    String cropPath = loader.getResource("com/filestack/sample_image_cropped.jpg").getPath();
    File cropFile = new File(cropPath);

    byte[] bytes = Okio.buffer(Okio.source(cropFile)).readByteArray();
    String correct = ByteString.of(sha256(bytes)).hex();
    byte[] actualBytes = transform.getContent().bytes();
    String output = ByteString.of(sha256(actualBytes)).hex();

    Assert.assertEquals(correct, output);
  }

  @Test
  public void testAvTransform() throws Exception {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    String oggPath = loader.getResource("com/filestack/sample_music.ogg").getPath();
    File oggFile = new File(oggPath);

    FileLink oggFileLink = client.upload(oggPath, false);
    HANDLES.add(oggFileLink.getHandle());

    AvTransformOptions options = new AvTransformOptions.Builder()
        .preset("mp3")
        .build();

    AvTransform transform = oggFileLink.avTransform(options);

    FileLink mp3FileLink;
    while ((mp3FileLink = transform.getFileLink()) == null) {
      Thread.sleep(5 * 1000);
    }
    HANDLES.add(mp3FileLink.getHandle());

    String mp3Path = loader.getResource("com/filestack/sample_music.mp3").getPath();
    File mp3File = new File(mp3Path);

    String correct = ByteString.of(sha256(Okio.buffer(Okio.source(mp3File)).readByteArray())).hex();
    byte[] bytes = mp3FileLink.getContent().bytes();
    String output = ByteString.of(bytes).hex();

    Assert.assertEquals(correct, output);
  }

  /** Deletes any files uploaded during tests. */
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

  /** Deletes any local files created during tests. */
  @AfterClass
  public static void cleanupFiles() {
    for (File file : FILES) {
      if (!file.delete()) {
        Assert.fail("Unable to cleanup resource");
      }
    }
  }

  private static byte[] sha256(byte[] bytes) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(bytes);
    return md.digest();
  }
}
