package com.filestack.model;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static com.filestack.util.MockConstants.*;

/**
 * Tests {@link FilestackClient FilestackClient} class.
 */
public class TestFilestackClient {

    @Test
    public void testInstantiation() {
        FilestackClient fsClient = new FilestackClient(API_KEY);
        assertNotNull("Unable to create FilestackClient", fsClient);
    }
}
