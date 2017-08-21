package com.filestack.errors;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestFilestackRuntimeException {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testNoArgs() throws FilestackRuntimeException {
    thrown.expect(FilestackRuntimeException.class);
    throw new FilestackRuntimeException();
  }

  @Test
  public void testMessage() throws FilestackRuntimeException {
    String message = "Error description... ";
    thrown.expect(FilestackRuntimeException.class);
    thrown.expectMessage(message);
    throw new FilestackRuntimeException(message);
  }

  @Test
  public void testCause() throws FilestackRuntimeException {
    Throwable cause = new Throwable();
    thrown.expect(FilestackRuntimeException.class);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new FilestackRuntimeException(cause);
  }

  @Test
  public void testMessageCause() throws FilestackRuntimeException {
    String message = "Error description... ";
    Throwable cause = new Throwable();
    thrown.expect(FilestackRuntimeException.class);
    thrown.expectMessage(message);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new FilestackRuntimeException(message, cause);
  }
}
