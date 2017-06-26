package util;

import exception.BadRequestException;
import exception.FilestackIOException;
import exception.HandleNotFoundException;
import exception.PolicySignatureException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Tests {@link FailedResponseInterceptor FailedResponseInterceptor} class to ensure correct exceptions are thrown.
 */
public class TestFailedResponseInterceptor {
    private static OkHttpClient client;

    @BeforeClass
    public static void setupClient() {
        client = new OkHttpClient.Builder()
                .addInterceptor(new FailedResponseInterceptor())
                .addInterceptor(new MockInterceptor())
                .build();
    }

    @Test(expected = BadRequestException.class)
    public void testBadRequestResponse() throws IOException {
        Request request = new Request.Builder()
                .url(MockInterceptor.TEST_BAD_REQUEST_URL)
                .build();

        client.newCall(request).execute();
    }

    @Test(expected = PolicySignatureException.class)
    public void testForbiddenResponse() throws IOException {
        Request request = new Request.Builder()
                .url(MockInterceptor.TEST_FORBIDDEN_URL)
                .build();

        client.newCall(request).execute();
    }

    @Test(expected = HandleNotFoundException.class)
    public void testNotFoundResponse() throws IOException {
        Request request = new Request.Builder()
                .url(MockInterceptor.TEST_NOT_FOUND_URL)
                .build();

        client.newCall(request).execute();
    }

    @Test(expected = FilestackIOException.class)
    public void testUnmatchedResponse() throws IOException {
        Request request = new Request.Builder()
                .url(MockInterceptor.TEST_UNMATCHED_URL)
                .build();

        client.newCall(request).execute();
    }
}
