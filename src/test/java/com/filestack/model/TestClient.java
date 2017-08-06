package com.filestack.model;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static com.filestack.util.MockConstants.*;

/**
 * Tests {@link Client Client} class.
 */
public class TestClient {

    @Test
    public void testInstantiation() {
        Client client = new Client(API_KEY);
        assertNotNull("Unable to create Client", client);
    }
}
