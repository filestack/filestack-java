package com.filestack.errors;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestInternalException {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testNoArgs() throws InternalException {
    thrown.expect(InternalException.class);
    throw new InternalException();
  }

  @Test
  public void testMessage() throws InternalException {
    String message = "Error description... ";
    thrown.expect(InternalException.class);
    thrown.expectMessage(message);
    throw new InternalException(message);
  }

  @Test
  public void testCause() throws InternalException {
    Throwable cause = new Throwable();
    thrown.expect(InternalException.class);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new InternalException(cause);
  }

  @Test
  public void testMessageCause() throws InternalException {
    String message = "Error description... ";
    Throwable cause = new Throwable();
    thrown.expect(InternalException.class);
    thrown.expectMessage(message);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new InternalException(message, cause);
  }
}
