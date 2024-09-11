package org.filestack.internal;

import org.filestack.HttpException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.filestack.UtilsKt.mockOkHttpResponse;

public class TestRetryNetworkFunc {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testNetworkFailure() throws Exception {
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(1, 0, 0) {

      @Override
      Response<Void> work() throws Exception {
        throw new IOException();
      }
    };

    thrown.expect(IOException.class);
    retryNetworkFunc.call();
  }

  @Test
  public void testServerFailure() throws Exception {
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(0, 1, 0) {

      @Override
      Response<Void> work() throws Exception {
        return Response.error(mockOkHttpResponse(500));
      }
    };

    thrown.expect(HttpException.class);
    retryNetworkFunc.call();
  }

  @Test
  public void test206() throws Exception {
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(0, 1, 0) {

      @Override
      Response<Void> work() throws Exception {
        return Response.error(mockOkHttpResponse(206));
      }
    };

    thrown.expect(HttpException.class);
    retryNetworkFunc.call();
  }

  @Test
  public void test400() throws Exception {
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(0, 1, 0) {

      @Override
      Response<Void> work() throws Exception {
        return Response.error(mockOkHttpResponse(400));
      }
    };

    thrown.expect(HttpException.class);
    retryNetworkFunc.call();
  }

  @Test
  public void test403() throws Exception {
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(0, 1, 0) {

      @Override
      Response<Void> work() throws Exception {
        return Response.error(mockOkHttpResponse(403));
      }
    };

    thrown.expect(HttpException.class);
    retryNetworkFunc.call();
  }

  @Test
  public void testNetworkRetryCount() {
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(5, 5, 0) {

      @Override
      Response<Void> work() throws Exception {
        throw new IOException();
      }
    };

    try {
      retryNetworkFunc.call();
    } catch (Exception e) {
      Assert.assertEquals(6, retryNetworkFunc.getNetworkRetries());
    }
  }

  @Test
  public void testServerRetryCount() {
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(5, 5, 0) {

      @Override
      Response<Void> work() throws Exception {
        return Response.error(mockOkHttpResponse(500));
      }
    };

    try {
      retryNetworkFunc.call();
    } catch (Exception e) {
      Assert.assertEquals(6, retryNetworkFunc.getServerRetries());
    }
  }
}
