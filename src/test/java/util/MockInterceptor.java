package util;


import exception.FilestackIOException;
import okhttp3.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Intercepts HTTP requests and returns mock responses for unit testing.
 */
public class MockInterceptor implements Interceptor {
    private static final String MOCK_BASE_URL = "https://mock.filestackapi.com/";
    private static final String TEST_HEADER_PATH = "test-header";
    private static final String TEST_FORBIDDEN_PATH = "test-forbidden";
    private static final String TEST_NOT_FOUND_PATH = "test-not-found";
    private static final String TEST_UNMATCHED_PATH = "test-unmatched";

    private static final String CDN_MOCK_FILENAME = "filestack_test.txt";
    private static final String CDN_MOCK_CONTENT = "Test content for handle: %s\n%s\n";
    private static final String CDN_MOCK_MIME = "text/plain; charset=utf-8";

    private static final String HEADER_FILENAME = "x-file-name";

    private static final int CODE_OK = 200;
    private static final int CODE_FORBIDDEN = 403;
    private static final int CODE_NOT_FOUND = 404;

    private static final String MESSAGE_OK = "OK";
    private static final String MESSAGE_FORBIDDEN = "FORBIDDEN";
    private static final String MESSAGE_NOT_FOUND = "NOT FOUND";

    public static final String TEST_HEADER_URL = MOCK_BASE_URL + TEST_HEADER_PATH;
    public static final String TEST_FORBIDDEN_URL = MOCK_BASE_URL + TEST_FORBIDDEN_PATH;
    public static final String TEST_NOT_FOUND_URL = MOCK_BASE_URL + TEST_NOT_FOUND_PATH;
    public static final String TEST_UNMATCHED_URL = MOCK_BASE_URL + TEST_UNMATCHED_PATH;

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl url = chain.request().url();
        Response.Builder builder;

        String host = url.host();
        List<String> path = url.pathSegments();

        if (FilestackService.Cdn.URL.contains(host))
            builder = genCdnResponse(path.get(0));
        else if (MOCK_BASE_URL.contains(host))
            builder = genSimpleResponse(path.get(0));
        else
            throw new FilestackIOException("Host not mocked");

        return builder
                .protocol(Protocol.HTTP_1_1)
                .request(chain.request())
                .build();
    }

    /**
     * Generates a test response for CDN requests.
     * Response contains data for a simple text file.
     *
     * @param handle File handle / id
     */
    private Response.Builder genCdnResponse(String handle) {
        String bodyText = String.format(CDN_MOCK_CONTENT, handle, new Date());
        ResponseBody body = ResponseBody.create(MediaType.parse(CDN_MOCK_MIME), bodyText);
        return new Response.Builder()
                .addHeader(HEADER_FILENAME, CDN_MOCK_FILENAME)
                .body(body)
                .code(CODE_OK)
                .message(MESSAGE_OK);
    }

    /**
     * Generates simple responses for specific unit tests.
     */
    private Response.Builder genSimpleResponse(String path) throws IOException {
        switch (path) {
            case TEST_HEADER_PATH:
                return new Response.Builder().code(CODE_OK).message(MESSAGE_OK);
            case TEST_FORBIDDEN_PATH:
                return new Response.Builder().code(CODE_FORBIDDEN).message(MESSAGE_FORBIDDEN);
            case TEST_NOT_FOUND_PATH:
                return new Response.Builder().code(CODE_NOT_FOUND).message(MESSAGE_NOT_FOUND);
            case TEST_UNMATCHED_PATH:
                return new Response.Builder().code(0).message("FAKE MESSAGE");
            default:
                throw new FilestackIOException("No path matched for mock host");
        }
    }
}
