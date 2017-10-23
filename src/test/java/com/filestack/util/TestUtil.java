package com.filestack.util;

import com.filestack.HttpException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import retrofit2.Response;

public class TestUtil extends Util {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testCheckResponseAndThrow() {
    int code = 400;
    String message = "Error message...";
    MediaType mediaType = MediaType.parse("text/plain");
    Response response = Response.error(code, ResponseBody.create(mediaType, message));

    try {
      Util.checkResponseAndThrow(response);
      Assert.fail("Should have thrown exception");
    } catch (HttpException e) {
      Assert.assertEquals(code, e.getCode());
      Assert.assertEquals(message, e.getMessage());
    } catch (IOException e) {
      Assert.fail("Threw wrong exception");
    }
  }

  @Test
  public void testCreateWriteFileSuccess() throws Exception {
    File file = Util.createWriteFile("/tmp/filestack_test_create_write_file.txt");
    if (!file.delete()) {
      Assert.fail("Unable to cleanup file");
    }
  }

  @Test
  public void testCreateWriteFileFailAccessNew() throws Exception {
    thrown.expect(IOException.class);
    Util.createWriteFile("/need_root_access.txt");
  }

  @Test
  public void testCreateWriteFileFailDirectory() throws Exception {
    thrown.expect(FileNotFoundException.class);
    thrown.expectMessage("Can't write to directory");
    Util.createWriteFile("/tmp");
  }

  @Test
  public void testCreateWriteFileFailSpecial() throws Exception {
    thrown.expect(FileNotFoundException.class);
    thrown.expectMessage("Can't write to special file");
    Util.createWriteFile("/dev/null");
  }

  @Test
  public void testCreateWriteFileFailAccessExisting() throws Exception {
    thrown.expect(FileNotFoundException.class);
    thrown.expectMessage("No write access");
    Util.createWriteFile("/etc/hosts");
  }

  @Test
  public void testCreateReadFileSuccess() throws Exception {
    Util.createReadFile("/etc/hosts");
  }

  @Test
  public void testCreateReadFileFailExists() throws Exception {
    thrown.expect(FileNotFoundException.class);
    thrown.expectMessage("/does_not_exist.txt");
    Util.createReadFile("/does_not_exist.txt");
  }

  @Test
  public void testCreateReadFileFailDirectory() throws Exception {
    thrown.expect(FileNotFoundException.class);
    thrown.expectMessage("Can't read from directory");
    Util.createReadFile("/tmp");
  }

  @Test
  public void testCreateReadFileFailSpecial() throws Exception {
    thrown.expect(FileNotFoundException.class);
    thrown.expectMessage("Can't read from special file");
    Util.createReadFile("/dev/null");
  }
}
