package com.filestack.util;

import static com.filestack.util.MockConstants.TEST_HEADER_URL;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

/**
 * Tests {@link HeaderInterceptor} class to check if headers are added.
 */
public class TestHeaderInterceptor {

    @Test
    public void testHeadersAdded() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new MockInterceptor())
                .build();

        Request original = new Request.Builder()
                .url(TEST_HEADER_URL)
                .build();

        Response response = client.newCall(original).execute();
        Request modified = response.request();

        String version = Util.getVersion();

        String headerUserAgent = modified.header(HeaderInterceptor.HEADER_USER_AGENT);
        String headerFilestackSource = modified.header(HeaderInterceptor.HEADER_FILESTACK_SOURCE);

        assertNotNull("Missing user agent header", headerUserAgent);
        assertNotNull("Missing filestack source header", headerFilestackSource);

        String correctUserAgent = String.format(HeaderInterceptor.USER_AGENT, version);
        String correctFilestackSource = String.format(HeaderInterceptor.FILESTACK_SOURCE, version);

        assertTrue("Incorrect user agent header",
                correctUserAgent.equals(headerUserAgent));
        assertTrue("Incorrect filestack source header",
                correctFilestackSource.equals(headerFilestackSource));
    }
}
