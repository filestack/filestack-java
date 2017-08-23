package com.filestack.errors;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestPolicySignatureException {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testNoArgs() throws PolicySignatureException {
    thrown.expect(PolicySignatureException.class);
    throw new PolicySignatureException();
  }

  @Test
  public void testMessage() throws PolicySignatureException {
    String message = "Error description... ";
    thrown.expect(PolicySignatureException.class);
    thrown.expectMessage(message);
    throw new PolicySignatureException(message);
  }

  @Test
  public void testCause() throws PolicySignatureException {
    Throwable cause = new Throwable();
    thrown.expect(PolicySignatureException.class);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new PolicySignatureException(cause);
  }

  @Test
  public void testMessageCause() throws PolicySignatureException {
    String message = "Error description... ";
    Throwable cause = new Throwable();
    thrown.expect(PolicySignatureException.class);
    thrown.expectMessage(message);
    thrown.expectCause(CoreMatchers.is(cause));
    throw new PolicySignatureException(message, cause);
  }
}
