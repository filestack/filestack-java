package model;

import com.google.common.io.Files;
import exception.PolicySignatureException;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import util.MockInterceptor;
import util.Networking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static util.MockConstants.*;

/**
 * Tests {@link FileLink FileLink} class.
 */
public class TestFileLink {
    private static final String DIRECTORY = "/tmp/";
    private static final String CUSTOM_FILENAME = "filestack_test_custom_filename.txt";
    private static final String OVERWRITE_PATHNAME = "/tmp/filestack_overwrite.txt";
    private static final String OVERWRITE_CONTENT = "Test overwrite content.";

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

    @Test
    public void testOverwrite() throws IOException {
        FileLink fileLink = new FileLink(API_KEY, HANDLE, SECURITY);

        // Setup test file to read from
        File file = new File(OVERWRITE_PATHNAME);
        file.createNewFile();
        Files.write(OVERWRITE_CONTENT.getBytes(), file);

        fileLink.overwrite(OVERWRITE_PATHNAME);
    }

    @Test(expected = PolicySignatureException.class)
    public void testOverwriteWithoutSecurity() throws IOException {
        FileLink fileLink = new FileLink(API_KEY, HANDLE);

        fileLink.overwrite(OVERWRITE_PATHNAME);
    }

    @Test(expected = FileNotFoundException.class)
    public void testOverwriteNoFile() throws IOException {
        FileLink fileLink = new FileLink(API_KEY, HANDLE, SECURITY);

        File file = new File(OVERWRITE_PATHNAME);
        file.delete();

        fileLink.overwrite(OVERWRITE_PATHNAME);
    }

    @Test
    public void testDelete() throws IOException {
        FileLink fileLink = new FileLink(API_KEY, HANDLE, SECURITY);

        fileLink.delete();
    }

    @Test(expected = PolicySignatureException.class)
    public void testDeleteWithoutSecurity() throws IOException {
        FileLink fileLink = new FileLink(API_KEY, HANDLE);

        fileLink.delete();
    }

    /**
     * Clear changes to {@link Networking Networking} class since it's a shared resource.
     */
    @AfterClass
    public static void teardown() {
        Networking.removeCustomClient();
    }
}
