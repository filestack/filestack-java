package model;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.junit.BeforeClass;
import org.junit.Test;
import util.Networking;
import util.MockInterceptor;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link FileLink FileLink} class.
 */
public class TestFileLink {
    private static final String API_KEY = "TEST_API_KEY";
    private static final String HANDLE = "TEST_FILE_HANDLE";
    private static final Policy POLICY = new Policy.Builder().expiry(4653651600L).build();
    private static final String APP_SECRET = "N3XOC2GP2NFTDCM43DZ6F2L6N4";
    private static final Security SECURITY = Security.createNew(POLICY, APP_SECRET);

    private static final String DIRECTORY = "/tmp/";
    private static final String CUSTOM_FILENAME = "filestack_test_custom_filename.txt";

    /**
     * Set a custom httpClient for our testing.
     * This custom client has an added interceptor to create mock responses.
     */
    @BeforeClass
    public static void setup() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new MockInterceptor())
                .build();
        Networking.setCustomClient(client);
    }

    @Test
    public void testInstantiation() {
        FileLink fileLink = new FileLink(API_KEY, HANDLE);
        assertNotNull("Unable to create FileLink", fileLink);
    }

    @Test
    public void testGetContent() throws IOException {
        FileLink fileLink = new FileLink(API_KEY, HANDLE);

        ResponseBody body = fileLink.getContent();
        String text = body.string();
        assertTrue("Unexpected content in response", text.contains("Test content"));
    }

    @Test
    public void testGetContentWithSecurity() throws IOException {
        FileLink fileLink = new FileLink(API_KEY, HANDLE, SECURITY);

        ResponseBody body = fileLink.getContent();
        String text = body.string();
        assertTrue("Unexpected content in response", text.contains("Test content"));
    }

    @Test
    public void testDownload() throws IOException {
        FileLink fileLink = new FileLink(API_KEY, HANDLE);

        File file = fileLink.download(DIRECTORY);
        assertTrue(file.isFile());
    }

    @Test
    public void testDownloadWithSecurity() throws IOException {
        FileLink fileLink = new FileLink(API_KEY, HANDLE, SECURITY);

        File file = fileLink.download(DIRECTORY);
        assertTrue(file.isFile());
    }

    @Test
    public void testDownloadCustomFilename() throws IOException {
        FileLink fileLink = new FileLink(API_KEY, HANDLE);
        File file = fileLink.download(DIRECTORY, CUSTOM_FILENAME);
        assertTrue(file.isFile());
    }

}
