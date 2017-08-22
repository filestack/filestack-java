package com.filestack.errors;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestFilestackException {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testNoArgs() throws FilestackException {
    thrown.expect(FilestackException.class);
    throw new FilestackException();
  }

  @Test
  public void testMessage() throws FilestackException {
    String message = "Error description... ";
    thrown.expect(FilestackException.class);
    thrown.expectMessage(message);
    throw new FilestackException(message);
  }

  @Test
  public void testCause() throws FilestackException {
    Throwable cause = new Throwable();
    thrown.expect(FilestackException.class);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new FilestackException(cause);
  }

  @Test
  public void testMessageCause() throws FilestackException {
    String message = "Error description... ";
    Throwable cause = new Throwable();
    thrown.expect(FilestackException.class);
    thrown.expectMessage(message);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new FilestackException(message, cause);
  }
}
