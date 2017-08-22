package com.filestack.errors;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestValidationException {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testNoArgs() throws ValidationException {
    thrown.expect(ValidationException.class);
    throw new ValidationException();
  }

  @Test
  public void testMessage() throws ValidationException {
    String message = "Error description... ";
    thrown.expect(ValidationException.class);
    thrown.expectMessage(message);
    throw new ValidationException(message);
  }

  @Test
  public void testCause() throws ValidationException {
    Throwable cause = new Throwable();
    thrown.expect(ValidationException.class);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new ValidationException(cause);
  }

  @Test
  public void testMessageCause() throws ValidationException {
    String message = "Error description... ";
    Throwable cause = new Throwable();
    thrown.expect(ValidationException.class);
    thrown.expectMessage(message);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new ValidationException(message, cause);
  }
}
