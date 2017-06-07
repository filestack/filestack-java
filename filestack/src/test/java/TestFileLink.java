import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Tests {@link FileLink FileLink} class.
 *
 * @author Shawn Aten (shawn@filestack.com)
 */
public class TestFileLink {
    private static final String HANDLE = "XXXXXXXXXXXXXXXXXXXXX";

    @Test
    public void testInstantiation() {
        FileLink fileLink = new FileLink(HANDLE);
        assertNotNull("Unable to create FileLink", fileLink);
    }
}
