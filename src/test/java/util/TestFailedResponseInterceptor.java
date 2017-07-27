package util;

import okhttp3.*;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link FailedResponseInterceptor FailedResponseInterceptor} class to ensure exceptions are thrown.
 */
public class TestFailedResponseInterceptor {

    @Test
    public void testWithBody() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                MediaType mediaType = MediaType.parse("text/plain");
                ResponseBody body = ResponseBody.create(mediaType, "test error body");

                return new Response.Builder()
                        .protocol(Protocol.HTTP_1_1)
                        .request(chain.request())
                        .code(400)
                        .message("Bad Request")
                        .body(body)
                        .build();
            }
        };

        OkHttpClient httpClient = Networking.getHttpClient().newBuilder().addInterceptor(interceptor).build();

        Request request = new Request.Builder().url("https://www.example.com").build();

        try {
            httpClient.newCall(request).execute();
        } catch (IOException e) {
            assertTrue(e.getMessage().equals("test error body"));
        }
    }

    @Test
    public void testWithoutBody() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                return new Response.Builder()
                        .protocol(Protocol.HTTP_1_1)
                        .request(chain.request())
                        .code(400)
                        .message("Bad Request")
                        .build();
            }
        };

        OkHttpClient httpClient = Networking.getHttpClient().newBuilder().addInterceptor(interceptor).build();

        Request request = new Request.Builder().url("https://www.example.com").build();

        try {
            httpClient.newCall(request).execute();
        } catch (IOException e) {
            assertTrue(e.getMessage().equals("www.example.com 400 Bad Request"));
        }
    }
}
