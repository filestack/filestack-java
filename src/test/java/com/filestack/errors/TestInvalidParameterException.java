package com.filestack.errors;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestInvalidParameterException {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testNoArgs() throws InvalidParameterException {
    thrown.expect(InvalidParameterException.class);
    throw new InvalidParameterException();
  }

  @Test
  public void testMessage() throws InvalidParameterException {
    String message = "Error description... ";
    thrown.expect(InvalidParameterException.class);
    thrown.expectMessage(message);
    throw new InvalidParameterException(message);
  }

  @Test
  public void testCause() throws InvalidParameterException {
    Throwable cause = new Throwable();
    thrown.expect(InvalidParameterException.class);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new InvalidParameterException(cause);
  }

  @Test
  public void testMessageCause() throws InvalidParameterException {
    String message = "Error description... ";
    Throwable cause = new Throwable();
    thrown.expect(InvalidParameterException.class);
    thrown.expectMessage(message);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new InvalidParameterException(message, cause);
  }
}
