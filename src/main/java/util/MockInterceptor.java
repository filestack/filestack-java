package util;


import okhttp3.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Intercepts HTTP requests and returns mock responses for unit testing.
 */
public class MockInterceptor implements Interceptor {
    public static String TEST_FILENAME = "filestack_test.txt";
    public static String TEST_FILE_CONTENT = "Test content for handle: %s\n%s\n";
    public static String TEST_FILE_TYPE = "text/plain; charset=utf-8";

    public static String HEADER_FILENAME = "x-file-name";

    public static String MESSAGE_OK = "OK";

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl url = chain.request().url();
        Response.Builder builder = null;

        String host = url.host();
        List<String> path = url.pathSegments();
        String query = url.query();

        if (CdnService.URL.contains(host)) {
            if (path.size() == 1) {
                String handle = path.get(0);
                builder = genGetResponse(handle);
            }
        }

        if (builder == null)
            throw new IOException("The request was not matched by the interceptor");

        return builder
                .protocol(Protocol.HTTP_1_1)
                .request(chain.request())
                .build();
    }

    /**
     * Generates a test response for get requests.
     * Response contains data for a simple text file.
     *
     * @param handle File handle / id
     */
    private Response.Builder genGetResponse(String handle) {
        String bodyText = String.format(TEST_FILE_CONTENT, handle, new Date());
        ResponseBody body = ResponseBody.create(MediaType.parse(TEST_FILE_TYPE), bodyText);
        return new Response.Builder()
                .addHeader(HEADER_FILENAME, TEST_FILENAME)
                .body(body)
                .code(200)
                .message(MESSAGE_OK);
    }
}
