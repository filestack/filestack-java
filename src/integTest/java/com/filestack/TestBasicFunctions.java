package com.filestack;

import com.google.common.io.Files;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.UUID;

public class TestBasicFunctions {
  private static final Config config = new Config.Builder()
      .apiKey(System.getenv("API_KEY"))
      .security(System.getenv("POLICY"), System.getenv("SIGNATURE"))
      .build();
  private static final Client client = new Client(config);

  private static final ArrayList<String> HANDLES = new ArrayList<>();
  private static final ArrayList<File> FILES = new ArrayList<>();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static File createRandomFile(String uuid) throws IOException {
    return createRandomFile(uuid, null);
  }

  private static File createRandomFile(String uuid, Long size) throws IOException {
    File file  = new File("/tmp/" + uuid + ".txt");
    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
    randomAccessFile.writeChars(uuid);
    if (size != null) {
      randomAccessFile.setLength(size);
    }
    randomAccessFile.close();
    return file;
  }

  @Test
  public void testUpload() throws Exception {
    String uuid = UUID.randomUUID().toString();
    File file = createRandomFile(uuid, 15L * 1024 * 1024);
    FILES.add(file);

    FileLink fileLink = client.upload(file.getPath(), false);
    String handle = fileLink.getHandle();
    HANDLES.add(handle);

    Assert.assertNotNull(handle);
  }

  @Test
  public void testGetContent() throws Exception {
    String uuid = UUID.randomUUID().toString();
    File file = createRandomFile(uuid);
    FILES.add(file);

    FileLink fileLink = client.upload(file.getPath(), false);
    String handle = fileLink.getHandle();
    HANDLES.add(handle);
    byte[] bytes = fileLink.getContent().bytes();
    String content = new String(bytes, "utf-16");

    Assert.assertEquals(uuid, content);
  }

  @Test
  public void testDownload() throws Exception {
    String uploadUuid = UUID.randomUUID().toString();
    File uploadFile = createRandomFile(uploadUuid);
    FILES.add(uploadFile);

    FileLink fileLink = client.upload(uploadFile.getPath(), false);
    String handle = fileLink.getHandle();
    HANDLES.add(handle);

    String downloadUuid = UUID.randomUUID().toString();
    File downloadFile = new File("/tmp/" + downloadUuid + ".txt");
    FILES.add(downloadFile);
    fileLink.download("/tmp/", downloadFile.getName());

    Assert.assertTrue(downloadFile.isFile());
    byte[] bytes = Files.asByteSource(downloadFile).read();
    String content = new String(bytes, "utf-16");
    Assert.assertEquals(uploadUuid, content);
  }

  @Test
  public void testOverwrite() throws Exception {
    String uploadUuid = UUID.randomUUID().toString();
    File uploadFile = createRandomFile(uploadUuid);
    FILES.add(uploadFile);

    String overwriteUuid = UUID.randomUUID().toString();
    File overwriteFile = createRandomFile(overwriteUuid);
    FILES.add(overwriteFile);

    FileLink fileLink = client.upload(uploadFile.getPath(), false);
    String handle = fileLink.getHandle();
    HANDLES.add(handle);

    fileLink.overwrite(overwriteFile.getPath());

    byte[] bytes = fileLink.getContent().bytes();
    String content = new String(bytes, "utf-16");
    Assert.assertEquals(overwriteUuid, content);
  }

  @Test
  public void testDelete() throws Exception {
    String uploadUuid = UUID.randomUUID().toString();
    File uploadFile = createRandomFile(uploadUuid);
    FILES.add(uploadFile);

    FileLink fileLink = client.upload(uploadFile.getPath(), false);
    fileLink.delete();

    thrown.expect(HttpException.class);
    fileLink.getContent();
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
