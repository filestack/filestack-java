package org.filestack.internal;

import org.filestack.HttpException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.filestack.UtilsKt.mockOkHttpResponse;
import static org.junit.Assert.*;

public class TestUtil extends Util {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testCheckResponseAndThrow() {
    int code = 400;
    String message = "Error message...";
    Response<Void> response = Response.error(mockOkHttpResponse(400, message));

    try {
      Util.checkResponseAndThrow(response);
      fail("Should have thrown exception");
    } catch (HttpException e) {
      assertEquals(code, e.getCode());
      assertEquals(message, e.getMessage());
    } catch (IOException e) {
      fail("Threw wrong exception");
    }
  }

  @Test
  public void testCreateWriteFileSuccess() throws Exception {
    File file = Util.createWriteFile("/tmp/filestack_test_create_write_file.txt");
    if (!file.delete()) {
      fail("Unable to cleanup file");
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

  @Test
  public void testIsNullOrEmpty() {
    assertTrue(Util.isNullOrEmpty(null));
    assertTrue(Util.isNullOrEmpty(""));
  }

  @Test
  public void testThrowIfNullOrEmpty() {
    String message = "some exception message";

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(message);

    Util.throwIfNullOrEmpty(null, message);
  }
}
