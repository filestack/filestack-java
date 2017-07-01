package model;

import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import util.MockInterceptor;
import util.Networking;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static util.MockConstants.*;

public class TestImageTransform {
    private static final String SOURCE = "https://example.com/image.jpg";

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
    public void testDebug() throws IOException {
        ImageTransform transform = new ImageTransform(FILE_LINK);
        transform = transform.resize(100, 100, null, null);
        JsonObject debugResponse = transform.debug();
        String message = "Debug response was null";
        assertTrue(message, debugResponse != null);
    }

    @Test
    public void testDebugExternal() throws IOException {
        ImageTransform transform = new ImageTransform(CLIENT, SOURCE);
        transform = transform.resize(100, 100, null, null);
        JsonObject debugResponse = transform.debug();
        String message = "External debug response was null";
        assertTrue(message, debugResponse != null);
    }

    /**
     * Clear changes to {@link Networking Networking} class since it's a shared resource.
     */
    @AfterClass
    public static void teardown() {
        Networking.removeCustomClient();
    }
}
