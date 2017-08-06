package com.filestack.util;

import com.filestack.model.Client;
import com.filestack.model.FileLink;
import com.filestack.model.Policy;
import com.filestack.model.Security;

/**
 * Constants for {@link MockInterceptor MockInterceptor} and unit tests.
 */
public class MockConstants {
    static final String MOCK_BASE_URL = "https://mock.filestackapi.com/";
    static final String TEST_HEADER_PATH = "test-header";
    static final String TEST_BAD_REQUEST_PATH = "test-bad-request";
    static final String TEST_FORBIDDEN_PATH = "test-forbidden";
    static final String TEST_NOT_FOUND_PATH = "test-not-found";
    static final String TEST_UNMATCHED_PATH = "test-unmatched";

    static final String CDN_MOCK_FILENAME = "filestack_test.txt";
    static final String CDN_MOCK_CONTENT = "Test content for handle: %s\n%s\n";

    static final String HEADER_FILENAME = "x-file-name";

    static final int CODE_OK = 200;
    static final int CODE_BAD_REQUEST = 400;
    static final int CODE_FORBIDDEN = 403;
    static final int CODE_NOT_FOUND = 404;

    static final String MESSAGE_OK = "OK";
    static final String MESSAGE_BAD_REQUEST = "BAD REQUEST";
    static final String MESSAGE_FORBIDDEN = "FORBIDDEN";
    static final String MESSAGE_NOT_FOUND = "NOT FOUND";

    static final String MIME_TEXT = "text/plain; charset=utf-8";
    static final String MIME_JSON = "application/json";

    static final String TEST_HEADER_URL = MOCK_BASE_URL + TEST_HEADER_PATH;
    static final String TEST_BAD_REQUEST_URL = MOCK_BASE_URL + TEST_BAD_REQUEST_PATH;
    static final String TEST_FORBIDDEN_URL = MOCK_BASE_URL + TEST_FORBIDDEN_PATH;
    static final String TEST_NOT_FOUND_URL = MOCK_BASE_URL + TEST_NOT_FOUND_PATH;
    static final String TEST_UNMATCHED_URL = MOCK_BASE_URL + TEST_UNMATCHED_PATH;

    public static final String API_KEY = "API_KEY";
    public static final String HANDLE = "HANDLE";
    public static final Policy POLICY = new Policy.Builder().expiry(4653651600L).build();
    public static final String APP_SECRET = "N3XOC2GP2NFTDCM43DZ6F2L6N4";
    public static final Security SECURITY = Security.createNew(POLICY, APP_SECRET);
    public static final FileLink FILE_LINK = new FileLink(API_KEY, HANDLE);
    public static final FileLink FILE_LINK_SECURITY = new FileLink(API_KEY, HANDLE, SECURITY);
    public static final Client CLIENT = new Client(API_KEY);
    public static final Client CLIENT_SECURITY = new Client(API_KEY, SECURITY);
}
