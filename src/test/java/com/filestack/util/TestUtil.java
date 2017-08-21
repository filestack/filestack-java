package com.filestack.util;

import com.filestack.errors.ValidationException;
import java.io.File;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestUtil {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testCreateWriteFileSuccess() throws ValidationException {
    File file = Util.createWriteFile("/tmp/filestack_test_create_write_file.txt");
    if (!file.delete()) {
      Assert.fail("Unable to cleanup file");
    }
  }

  @Test
  public void testCreateWriteFileFailUnable() throws ValidationException {
    thrown.expect(ValidationException.class);
    thrown.expectMessage("Unable to create file");
    Util.createWriteFile("/need_root_access.txt");
  }

  @Test
  public void testCreateWriteFileFailDirectory() throws ValidationException {
    thrown.expect(ValidationException.class);
    thrown.expectMessage("Can't write to directory");
    Util.createWriteFile("/tmp");
  }

  @Test
  public void testCreateWriteFileFailSpecial() throws ValidationException {
    thrown.expect(ValidationException.class);
    thrown.expectMessage("Can't write to special file");
    Util.createWriteFile("/dev/null");
  }

  @Test
  public void testCreateWriteFileFailAccess() throws ValidationException {
    thrown.expect(ValidationException.class);
    thrown.expectMessage("No write access");
    Util.createWriteFile("/etc/hosts");
  }

  @Test
  public void testCreateReadFileSuccess() throws ValidationException {
    Util.createReadFile("/etc/hosts");
  }

  @Test
  public void testCreateReadFileFailExists() throws ValidationException {
    thrown.expect(ValidationException.class);
    thrown.expectMessage("File doesn't exist");
    Util.createReadFile("/does_not_exist.txt");
  }

  @Test
  public void testCreateReadFileFailDirectory() throws ValidationException {
    thrown.expect(ValidationException.class);
    thrown.expectMessage("Can't read from directory");
    Util.createReadFile("/tmp");
  }

  @Test
  public void testCreateReadFileFailSpecial() throws ValidationException {
    thrown.expect(ValidationException.class);
    thrown.expectMessage("Can't read from special file");
    Util.createReadFile("/dev/null");
  }
}
