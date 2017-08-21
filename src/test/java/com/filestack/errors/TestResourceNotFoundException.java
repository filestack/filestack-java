package com.filestack.errors;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestResourceNotFoundException {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testNoArgs() throws ResourceNotFoundException {
    thrown.expect(ResourceNotFoundException.class);
    throw new ResourceNotFoundException();
  }

  @Test
  public void testMessage() throws ResourceNotFoundException {
    String message = "Error description... ";
    thrown.expect(ResourceNotFoundException.class);
    thrown.expectMessage(message);
    throw new ResourceNotFoundException(message);
  }

  @Test
  public void testCause() throws ResourceNotFoundException {
    Throwable cause = new Throwable();
    thrown.expect(ResourceNotFoundException.class);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new ResourceNotFoundException(cause);
  }

  @Test
  public void testMessageCause() throws ResourceNotFoundException {
    String message = "Error description... ";
    Throwable cause = new Throwable();
    thrown.expect(ResourceNotFoundException.class);
    thrown.expectMessage(message);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new ResourceNotFoundException(message, cause);
  }
}
