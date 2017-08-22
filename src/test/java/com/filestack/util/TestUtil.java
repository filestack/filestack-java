package com.filestack.util;

import com.filestack.errors.FilestackException;
import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ResourceNotFoundException;
import com.filestack.errors.ValidationException;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import retrofit2.Response;

public class TestUtil extends Util {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testCheckResponseAndThrow400() throws FilestackException {
    MediaType mediaType = MediaType.parse("text/plain");
    Response response = Response.error(400, ResponseBody.create(mediaType, "test"));
    thrown.expect(InvalidParameterException.class);
    Util.checkResponseAndThrow(response);
  }

  @Test
  public void testCheckResponseAndThrow403() throws FilestackException {
    MediaType mediaType = MediaType.parse("text/plain");
    Response response = Response.error(403, ResponseBody.create(mediaType, "test"));
    thrown.expect(PolicySignatureException.class);
    Util.checkResponseAndThrow(response);
  }

  @Test
  public void testCheckResponseAndThrow404() throws FilestackException {
    MediaType mediaType = MediaType.parse("text/plain");
    Response response = Response.error(404, ResponseBody.create(mediaType, "test"));
    thrown.expect(ResourceNotFoundException.class);
    Util.checkResponseAndThrow(response);
  }

  @Test
  public void testCheckResponseAndThrow500() throws FilestackException {
    MediaType mediaType = MediaType.parse("text/plain");
    Response response = Response.error(500, ResponseBody.create(mediaType, "test"));
    thrown.expect(InternalException.class);
    Util.checkResponseAndThrow(response);
  }

  @Test
  public void testCastExceptionAndThrowInvalid() throws FilestackException, IOException {
    Exception e = new InvalidParameterException();
    thrown.expect(InvalidParameterException.class);
    Util.castExceptionAndThrow(e);
  }

  @Test
  public void testCastExceptionAndThrowIo() throws FilestackException, IOException {
    Exception e = new IOException();
    thrown.expect(IOException.class);
    Util.castExceptionAndThrow(e);
  }

  @Test
  public void testCastExceptionAndThrowPolicy() throws FilestackException, IOException {
    Exception e = new PolicySignatureException();
    thrown.expect(PolicySignatureException.class);
    Util.castExceptionAndThrow(e);
  }

  @Test
  public void testCastExceptionAndThrowResource() throws FilestackException, IOException {
    Exception e = new ResourceNotFoundException();
    thrown.expect(ResourceNotFoundException.class);
    Util.castExceptionAndThrow(e);
  }

  @Test
  public void testCastExceptionAndThrowInternal() throws FilestackException, IOException {
    Exception e = new InternalException();
    thrown.expect(InternalException.class);
    Util.castExceptionAndThrow(e);
  }

  @Test
  public void testCastExceptionAndThrowUnknown() throws FilestackException, IOException {
    Exception e = new Exception();
    thrown.expect(InternalException.class);
    thrown.expectCause(CoreMatchers.is(e));
    Util.castExceptionAndThrow(e);
  }

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
