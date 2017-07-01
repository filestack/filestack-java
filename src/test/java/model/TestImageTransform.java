package model;

import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import util.FilestackService;
import util.MockInterceptor;
import util.Networking;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static util.MockConstants.*;

public class TestImageTransform {
    private static final String RESIZE_TASK_STRING = "resize=width:100,height:100";
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

    public void testDebugUrl() throws IOException {
        FilestackService.Process processService = Networking.getProcessService();

        String correct = FilestackService.Process.URL + "debug/" + RESIZE_TASK_STRING + "/" + HANDLE;
        String output = processService.debug(RESIZE_TASK_STRING, HANDLE).request().url().toString();

        String message = String.format("Debug URL malformed\nCorrect: %s\nOutput:  %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    @Test
    public void testDebugExternal() throws IOException {
        ImageTransform transform = new ImageTransform(CLIENT, SOURCE);
        transform = transform.resize(100, 100, null, null);
        JsonObject debugResponse = transform.debug();
        String message = "External debug response was null";
        assertTrue(message, debugResponse != null);
    }

    public void testDebugExternalUrl() throws IOException {
        FilestackService.Process processService = Networking.getProcessService();

        String correct = FilestackService.Process.URL + API_KEY + "/debug/" + RESIZE_TASK_STRING + "/" + SOURCE;
        String output = processService.debugExternal(API_KEY, RESIZE_TASK_STRING, SOURCE).request().url().toString();

        String message = String.format("External debug URL malformed\nCorrect: %s\nOutput:  %s", correct, output);
        assertTrue(message, output.equals(correct));
    }

    /**
     * Clear changes to {@link Networking Networking} class since it's a shared resource.
     */
    @AfterClass
    public static void teardown() {
        Networking.removeCustomClient();
    }
}
