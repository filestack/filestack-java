package com.filestack.util;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link HeaderInterceptor} class to check if headers are added.
 */
public class TestHeaderInterceptor {

  @Test
  public void testHeadersAdded() throws IOException {
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new HeaderInterceptor())
        .addInterceptor(new Interceptor() {
          @Override
          public Response intercept(Chain chain) throws IOException {
            return new Response.Builder()
                .code(200)
                .message("OK")
                .protocol(Protocol.HTTP_1_1)
                .request(chain.request())
                .build();
          }
        })
        .build();

    Request original = new Request.Builder().url("https://example.com/").build();

    Response response = client.newCall(original).execute();
    Request modified = response.request();

    String version = Util.getVersion();

    String headerUserAgent = modified.header(HeaderInterceptor.HEADER_USER_AGENT);
    String headerFilestackSource = modified.header(HeaderInterceptor.HEADER_FILESTACK_SOURCE);

    Assert.assertNotNull(headerUserAgent);
    Assert.assertNotNull(headerFilestackSource);

    String correctUserAgent = String.format(HeaderInterceptor.USER_AGENT, version);
    String correctFilestackSource = String.format(HeaderInterceptor.FILESTACK_SOURCE, version);

    Assert.assertEquals(correctUserAgent, headerUserAgent);
    Assert.assertEquals(correctFilestackSource, headerFilestackSource);
  }
}
