package com.filestack;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class TestBasicFunctions {
  private static final String API_KEY = System.getenv("API_KEY");
  private static final String POLICY = System.getenv("POLICY");
  private static final String SIGNATURE = System.getenv("SIGNATURE");
  private static final Security SECURITY = Security.fromExisting(POLICY, SIGNATURE);

  private static ArrayList<String> handles = new ArrayList<>();
  private static ArrayList<File> files = new ArrayList<>();

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
    FilestackClient client = new FilestackClient(API_KEY, SECURITY);

    String uuid = UUID.randomUUID().toString();
    File file = createRandomFile(uuid, 15L * 1024 * 1024);
    files.add(file);

    FileLink fileLink = client.upload(file.getPath());
    String handle = fileLink.getHandle();
    handles.add(handle);

    Assert.assertNotNull(handle);
  }

  @Test
  public void testGetContent() throws Exception {
    FilestackClient client = new FilestackClient(API_KEY, SECURITY);

    String uuid = UUID.randomUUID().toString();
    File file = createRandomFile(uuid);
    files.add(file);

    FileLink fileLink = client.upload(file.getPath());
    String handle = fileLink.getHandle();
    handles.add(handle);
    byte[] bytes = fileLink.getContent().bytes();
    String content = new String(bytes, "utf-16");

    Assert.assertEquals(uuid, content);
  }

  @Test
  public void testDownload() throws Exception {
    FilestackClient client = new FilestackClient(API_KEY, SECURITY);

    String uploadUuid = UUID.randomUUID().toString();
    File uploadFile = createRandomFile(uploadUuid);
    files.add(uploadFile);

    FileLink fileLink = client.upload(uploadFile.getPath());
    String handle = fileLink.getHandle();
    handles.add(handle);

    String downloadUuid = UUID.randomUUID().toString();
    File downloadFile = new File("/tmp/" + downloadUuid + ".txt");
    files.add(downloadFile);
    fileLink.download("/tmp/", downloadFile.getName());

    Assert.assertTrue(downloadFile.isFile());
    byte[] bytes = Files.asByteSource(downloadFile).read();
    String content = new String(bytes, "utf-16");
    Assert.assertEquals(uploadUuid, content);
  }

  @Test
  public void testOverwrite() throws Exception {
    FilestackClient client = new FilestackClient(API_KEY, SECURITY);

    String uploadUuid = UUID.randomUUID().toString();
    File uploadFile = createRandomFile(uploadUuid);
    files.add(uploadFile);

    String overwriteUuid = UUID.randomUUID().toString();
    File overwriteFile = createRandomFile(overwriteUuid);
    files.add(overwriteFile);

    FileLink fileLink = client.upload(uploadFile.getPath());
    String handle = fileLink.getHandle();
    handles.add(handle);

    fileLink.overwrite(overwriteFile.getPath());

    byte[] bytes = fileLink.getContent().bytes();
    String content = new String(bytes, "utf-16");
    Assert.assertEquals(overwriteUuid, content);
  }

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

  @AfterClass
  public static void cleanupFiles() {
    for (File file : files) {
      if (!file.delete()) {
        Assert.fail("Unable to cleanup resource");
      }
    }
  }
}
