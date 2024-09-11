package org.filestack.internal;

import okhttp3.*;
import okhttp3.Response;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

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
                .body(ResponseBody.create(MediaType.get("text/plain"), "foo".getBytes()))
                .protocol(Protocol.HTTP_1_1)
                .request(chain.request())
                .build();
          }
        })
        .build();

    Request original = new Request.Builder().url("https://example.com/").build();

    Response response = client.newCall(original).execute();
    Request modified = response.request();

    String headerUserAgent = modified.header(HeaderInterceptor.HEADER_USER_AGENT);
    String headerFilestackSource = modified.header(HeaderInterceptor.HEADER_FILESTACK_SOURCE);

    assertEquals(HeaderInterceptor.USER_AGENT, headerUserAgent);
    assertEquals(HeaderInterceptor.FILESTACK_SOURCE, headerFilestackSource);
  }
}
