package model;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Tests {@link FileLink FileLink} class.
 */
public class TestFileLink {
    private static final String API_KEY = "XXXXXXXXXXXXXXXXXXXXXXX";
    private static final String HANDLE = "XXXXXXXXXXXXXXXXXXXXX";

    @Test
    public void testInstantiation() {
        FileLink fileLink = new FileLink(API_KEY, HANDLE);
        assertNotNull("Unable to create FileLink", fileLink);
    }
}
