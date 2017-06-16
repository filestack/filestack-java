package model;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import util.HeaderInterceptor;
import util.Networking;
import util.Util;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link util.HeaderInterceptor HeaderInterceptor} class to check if headers are added.
 */
public class TestHeaderInterceptor {
    private static final String API_KEY = "TEST_API_KEY";
    private static final String HANDLE = "TEST_FILE_HANDLE";

    private static final String TEST_URL = "https://filestack.com";

    /**
     * Set a custom httpClient for our testing.
     * This custom client has an added interceptor to create mock responses.
     */
    @BeforeClass
    public static void setup() {
        Networking.setMockMode(true);
    }

    @Test
    public void testHeadersAdded() throws IOException {
        OkHttpClient client = Networking.getHttpClient();
        Request original = new Request.Builder()
                .url(TEST_URL)
                .build();

        Response response = client.newCall(original).execute();
        Request modified = response.request();

        String version = Util.getVersion();

        String correctUserAgent = String.format(HeaderInterceptor.USER_AGENT, version);
        String correctFilestackSource = String.format(HeaderInterceptor.FILESTACK_SOURCE, version);

        String headerUserAgent = modified.header(HeaderInterceptor.HEADER_USER_AGENT);
        String headerFilestackSource = modified.header(HeaderInterceptor.HEADER_FILESTACK_SOURCE);

        assertNotNull("Missing user agent header", headerUserAgent);
        assertNotNull("Missing filestack source header", headerFilestackSource);

        assertTrue("Incorrect user agent header", correctUserAgent.equals(headerUserAgent));
        assertTrue("Incorrect filestack source header", correctFilestackSource.equals(headerFilestackSource));
    }
}
