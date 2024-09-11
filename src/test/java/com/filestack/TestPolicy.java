package org.filestack;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link Policy Policy} class to check building and converting to JSON.
 */
public class TestPolicy {
  private static final String TEST_SECRET = "RDOQO4WFRBFPRCFZOZFHJGZHW4";
  private static final String POLICY_NORMAL
      = "eyJleHBpcnkiOjQ2NTM2NTE2MDAsImNhbGxzIjpbIndyaXRlIiwicmVtb3ZlIl0sImhhbmRsZSI6IktXOUVKaFl0U"
      + "zZ5NDhXaG0yUzZEIiwidXJsIjoiaHR0cHM6Ly91cGxvYWRcXC53aWtpbWVkaWFcXC5vcmcvd2lraXBlZGlhLy4qIi"
      + "wibWF4U2l6ZSI6MTAyNCwibWluU2l6ZSI6MTI4LCJwYXRoIjoiL3NvbWUvZGlyLyIsImNvbnRhaW5lciI6InNvbWU"
      + "tY29udGFpbmVyIn0=";
  private static final String SIGNATURE_NORMAL
      = "506bd728a25588c6692565b0076889097e5d23971bcf1d1de646f4426d2dc9b6";
  private static final String POLICY_NO_CALLS = "eyJleHBpcnkiOjQ2NTM2NTE2MDB9";
  private static final String SIGNATURE_NO_CALLS
      = "ab1bea1b6ce36f77ff2a0a4da25651e64dfd9daeb0b2eacfe3836a13c96c022c";
  private static final String POLICY_FULL = "eyJleHBpcnkiOjE1NDA5MjUyMzYsImNhbGxzIjpbInBpY2siLCJyZ"
      + "WFkIiwic3RhdCIsIndyaXRlIiwid3JpdGVVcmwiLCJzdG9yZSIsImNvbnZlcnQiLCJyZW1vdmUiLCJleGlmIl19";
  private static final String SIGNATURE_FULL
      = "589352c67bec6999108060af07027175cade89db4a1296f7f7a603df72240edc";

  @Test
  public void testNormal() {
    Policy policy = new Policy.Builder()
        .expiry(4653651600L)
        .calls(Policy.CALL_WRITE, Policy.CALL_REMOVE)
        .handle("KW9EJhYtS6y48Whm2S6D")
        .url("https://upload\\.wikimedia\\.org/wikipedia/.*")
        .maxSize(1024)
        .minSize(128)
        .path("/some/dir/")
        .container("some-container")
        .build(TEST_SECRET);

    Assert.assertEquals(POLICY_NORMAL, policy.getEncodedPolicy());
    Assert.assertEquals(SIGNATURE_NORMAL, policy.getSignature());
  }

  @Test
  public void testNoCalls() {
    Policy policy = new Policy.Builder()
        .expiry(4653651600L)
        .build(TEST_SECRET);

    Assert.assertEquals(POLICY_NO_CALLS, policy.getEncodedPolicy());
    Assert.assertEquals(SIGNATURE_NO_CALLS, policy.getSignature());
  }
}
