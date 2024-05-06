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
    try {
      File file = Util.createWriteFile("/tmp/filestack_test_create_write_file.txt");
      if (!file.delete()) {
        fail("Unable to cleanup file");
      }
    } catch (IOException e) {
      e.printStackTrace(); // Example: print stack trace
    }
  }

  @Test
  public void testCreateWriteFileFailAccessNew() throws Exception {
    try {
      File file = Util.createWriteFile("/need_root_access.txt");
      if (!file.delete()) {
        fail("Unable to cleanup file");
      }
    } catch (IOException e) {
      e.printStackTrace(); // Example: print stack trace
    }
  }

  @Test
  public void testCreateWriteFileFailDirectory() throws Exception {
    try {
      File file = Util.createWriteFile("/tmp");
      if (!file.delete()) {
        fail("Can't write to directory");
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace(); // Example: print stack trace
    }
  }

  @Test
  public void testCreateWriteFileFailSpecial() throws Exception {
    try {
      File file = Util.createWriteFile("/dev/null");
      if (!file.delete()) {
        fail("Can't write to special file");
      }
    } catch (IOException e) {
      e.printStackTrace(); // Example: print stack trace
    }
  }

  @Test
  public void testCreateWriteFileFailAccessExisting() throws Exception {
    try {
      File file = Util.createWriteFile("/etc/hosts");
      if (!file.delete()) {
        fail("No write access");
      }
    } catch (IOException e) {
      e.printStackTrace(); // Example: print stack trace
    }
  }

  @Test
  public void testCreateReadFileSuccess() throws Exception {
    try {
      File file = Util.createReadFile("/etc/hosts");
      if (!file.delete()) {
        fail("/etc/hosts");
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace(); // Example: print stack trace
    }
  }

  @Test
  public void testCreateReadFileFailExists() throws Exception {
    try {
      File file = Util.createReadFile("/does_not_exist.txt");
      if (!file.delete()) {
        fail("/does_not_exist.txt");
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace(); // Example: print stack trace
    }
  }

  @Test
  public void testCreateReadFileFailDirectory() throws Exception {
    try {
      File file = Util.createReadFile("/tmp");
      if (!file.delete()) {
        fail("Can't read from directory");
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace(); // Example: print stack trace
    }
  }

  @Test
  public void testCreateReadFileFailSpecial() throws Exception {
    try {
      File file = Util.createReadFile("/dev/null");
      if (!file.delete()) {
        fail("Can't read from special file");
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace(); // Example: print stack trace
    }
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
