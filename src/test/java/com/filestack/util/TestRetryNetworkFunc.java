package com.filestack.util;

import com.filestack.errors.InternalException;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import retrofit2.Response;

public class TestRetryNetworkFunc {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testNetworkFailure() throws Exception {
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(5, 5, 0) {

      @Override
      Response work() throws Exception {
        throw new IOException();
      }
    };

    thrown.expect(IOException.class);
    retryNetworkFunc.call();

    Assert.assertEquals("Expected 5 network retries", 5, retryNetworkFunc.getNetworkRetries());
  }

  @Test
  public void testServerFailure() throws Exception {
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(5, 5, 0) {

      @Override
      Response work() throws Exception {
        MediaType mediaType = MediaType.parse("text/plain");
        return Response.error(500, ResponseBody.create(mediaType, "test"));
      }
    };

    thrown.expect(InternalException.class);
    retryNetworkFunc.call();

    Assert.assertEquals("Expected 5 server retries", 5, retryNetworkFunc.getServerRetries());
  }
}
