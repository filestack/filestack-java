package com.filestack.util;

import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
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
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(1, 0, 0) {

      @Override
      Response work() throws Exception {
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
      Response work() throws Exception {
        MediaType mediaType = MediaType.parse("text/plain");
        return Response.error(500, ResponseBody.create(mediaType, "test"));
      }
    };

    thrown.expect(InternalException.class);
    retryNetworkFunc.call();
  }

  @Test
  public void test206() throws Exception {
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(0, 1, 0) {

      @Override
      Response work() throws Exception {
        MediaType mediaType = MediaType.parse("text/plain");
        Request request = new Request.Builder()
            .url("https://example.com")
            .build();
        ResponseBody responseBody = ResponseBody.create(mediaType, "test");
        okhttp3.Response rawResponse = new okhttp3.Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(206)
            .message("Partial Content")
            .build();
        return Response.success(responseBody, rawResponse);
      }
    };

    thrown.expect(InternalException.class);
    retryNetworkFunc.call();
  }

  @Test
  public void test400() throws Exception {
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(0, 1, 0) {

      @Override
      Response work() throws Exception {
        MediaType mediaType = MediaType.parse("text/plain");
        return Response.error(400, ResponseBody.create(mediaType, "test"));
      }
    };

    thrown.expect(InvalidParameterException.class);
    retryNetworkFunc.call();
  }

  @Test
  public void test403() throws Exception {
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(0, 1, 0) {

      @Override
      Response work() throws Exception {
        MediaType mediaType = MediaType.parse("text/plain");
        return Response.error(403, ResponseBody.create(mediaType, "test"));
      }
    };

    thrown.expect(PolicySignatureException.class);
    retryNetworkFunc.call();
  }

  @Test
  public void testNetworkRetryCount() {
    RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(5, 5, 0) {

      @Override
      Response work() throws Exception {
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
      Response work() throws Exception {
        MediaType mediaType = MediaType.parse("text/plain");
        return Response.error(500, ResponseBody.create(mediaType, "test"));
      }
    };

    try {
      retryNetworkFunc.call();
    } catch (Exception e) {
      Assert.assertEquals(6, retryNetworkFunc.getServerRetries());
    }
  }
}
