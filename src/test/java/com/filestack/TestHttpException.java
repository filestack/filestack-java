package org.filestack;

import org.junit.Assert;
import org.junit.Test;

public class TestHttpException {

  @Test
  public void testCode() {
    int code = 400;
    HttpException exception = new HttpException(code);
    Assert.assertEquals(400, exception.getCode());
    Assert.assertNull(exception.getMessage());
  }

  @Test
  public void testCodeMessage() {
    int code = 400;
    String message = "Error description... ";
    HttpException exception = new HttpException(code, message);
    Assert.assertEquals(400, exception.getCode());
    Assert.assertEquals(message, exception.getMessage());
  }
}
