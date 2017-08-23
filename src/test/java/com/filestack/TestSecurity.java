package com.filestack;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link Security Security} class to check encoding and signing.
 */
public class TestSecurity {

  @Test
  public void testCreateNew() {
    Policy policy = new Policy.Builder().expiry(4653651600L).build();
    Security security = Security.createNew(policy, "N3XOC2GP2NFTDCM43DZ6F2L6N4");

    Assert.assertEquals("eyJleHBpcnkiOjQ2NTM2NTE2MDB9", security.getPolicy());
    Assert.assertEquals("d0dcacc68d00b7d2cd18c7f82aaf5bf172fdb423dc3cf0540d0da04912867e13",
        security.getSignature());
  }

  @Test
  public void testFromExisting() {
    Security security = Security.fromExisting("<policy>", "<signature>");

    Assert.assertEquals("<policy>", security.getPolicy());
    Assert.assertEquals("<signature>", security.getSignature());
  }
}
