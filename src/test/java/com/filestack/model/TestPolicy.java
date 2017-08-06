package com.filestack.model;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link Policy Policy} class to check building and converting to JSON.
 */
public class TestPolicy {
    private static final String CORRECT_JSON_POLICY = "{\"expiry\":4653651600,"
            + "\"call\":[\"write\",\"remove\"],"
            + "\"handle\":\"KW9EJhYtS6y48Whm2S6D\","
            + "\"url\":\"https://upload\\\\.wikimedia\\\\.org/wikipedia/.*\","
            + "\"maxSize\":1024,\"minSize\":128,"
            + "\"path\":\"/some/dir/\",\"container\":\"some-container\"}";

    @Test
    public void testBuild() {
        Gson gson = new Gson();

        Policy policy = new Policy.Builder()
                .expiry(4653651600L)
                .addCall(Policy.CALL_WRITE)
                .addCall(Policy.CALL_REMOVE)
                .handle("KW9EJhYtS6y48Whm2S6D")
                .url("https://upload\\.wikimedia\\.org/wikipedia/.*")
                .maxSize(1024)
                .minSize(128)
                .path("/some/dir/")
                .container("some-container")
                .build();
        String jsonPolicy = gson.toJson(policy);

        assertTrue("Incorrect JSON policy", CORRECT_JSON_POLICY.equals(jsonPolicy));
    }
}
