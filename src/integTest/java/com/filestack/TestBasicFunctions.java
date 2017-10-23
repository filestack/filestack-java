package com.filestack;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestBasicFunctions {
  private static final String API_KEY = System.getenv("API_KEY");
  private static final String POLICY = System.getenv("POLICY");
  private static final String SIGNATURE = System.getenv("SIGNATURE");
  private static final Security SECURITY = Security.fromExisting(POLICY, SIGNATURE);

  private static ArrayList<String> handles = new ArrayList<>();
  private static ArrayList<File> files = new ArrayList<>();

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
    FsClient client = new FsClient(API_KEY, SECURITY);

    String uuid = UUID.randomUUID().toString();
    File file = createRandomFile(uuid, 15L * 1024 * 1024);
    files.add(file);

    FsFile fsFile = client.upload(file.getPath(), false);
    String handle = fsFile.getHandle();
    handles.add(handle);

    Assert.assertNotNull(handle);
  }

  @Test
  public void testGetContent() throws Exception {
    FsClient client = new FsClient(API_KEY, SECURITY);

    String uuid = UUID.randomUUID().toString();
    File file = createRandomFile(uuid);
    files.add(file);

    FsFile fsFile = client.upload(file.getPath(), false);
    String handle = fsFile.getHandle();
    handles.add(handle);
    byte[] bytes = fsFile.getContent().bytes();
    String content = new String(bytes, "utf-16");

    Assert.assertEquals(uuid, content);
  }

  @Test
  public void testDownload() throws Exception {
    FsClient client = new FsClient(API_KEY, SECURITY);

    String uploadUuid = UUID.randomUUID().toString();
    File uploadFile = createRandomFile(uploadUuid);
    files.add(uploadFile);

    FsFile fsFile = client.upload(uploadFile.getPath(), false);
    String handle = fsFile.getHandle();
    handles.add(handle);

    String downloadUuid = UUID.randomUUID().toString();
    File downloadFile = new File("/tmp/" + downloadUuid + ".txt");
    files.add(downloadFile);
    fsFile.download("/tmp/", downloadFile.getName());

    Assert.assertTrue(downloadFile.isFile());
    byte[] bytes = Files.asByteSource(downloadFile).read();
    String content = new String(bytes, "utf-16");
    Assert.assertEquals(uploadUuid, content);
  }

  @Test
  public void testOverwrite() throws Exception {
    FsClient client = new FsClient(API_KEY, SECURITY);

    String uploadUuid = UUID.randomUUID().toString();
    File uploadFile = createRandomFile(uploadUuid);
    files.add(uploadFile);

    String overwriteUuid = UUID.randomUUID().toString();
    File overwriteFile = createRandomFile(overwriteUuid);
    files.add(overwriteFile);

    FsFile fsFile = client.upload(uploadFile.getPath(), false);
    String handle = fsFile.getHandle();
    handles.add(handle);

    fsFile.overwrite(overwriteFile.getPath());

    byte[] bytes = fsFile.getContent().bytes();
    String content = new String(bytes, "utf-16");
    Assert.assertEquals(overwriteUuid, content);
  }

  @Test
  public void testDelete() throws Exception {
    FsClient client = new FsClient(API_KEY, SECURITY);

    String uploadUuid = UUID.randomUUID().toString();
    File uploadFile = createRandomFile(uploadUuid);
    files.add(uploadFile);

    FsFile fsFile = client.upload(uploadFile.getPath(), false);

    fsFile.delete();

    thrown.expect(HttpException.class);
    fsFile.getContent();
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
