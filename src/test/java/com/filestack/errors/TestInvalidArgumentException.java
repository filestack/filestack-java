package com.filestack.errors;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestInvalidArgumentException {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testNoArgs() throws InvalidArgumentException {
    thrown.expect(InvalidArgumentException.class);
    throw new InvalidArgumentException();
  }

  @Test
  public void testMessage() throws InvalidArgumentException {
    String message = "Error description... ";
    thrown.expect(InvalidArgumentException.class);
    thrown.expectMessage(message);
    throw new InvalidArgumentException(message);
  }

  @Test
  public void testCause() throws InvalidArgumentException {
    Throwable cause = new Throwable();
    thrown.expect(InvalidArgumentException.class);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new InvalidArgumentException(cause);
  }

  @Test
  public void testMessageCause() throws InvalidArgumentException {
    String message = "Error description... ";
    Throwable cause = new Throwable();
    thrown.expect(InvalidArgumentException.class);
    thrown.expectMessage(message);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new InvalidArgumentException(message, cause);
  }
}
