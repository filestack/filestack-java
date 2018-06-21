package com.filestack;

import org.junit.Assert;
import org.junit.Test;

public class TestFail {

  @Test
  public void testFail() throws Exception {
    Assert.fail("this test failed");
  }
}
