package model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link Security Security} class to check encoding and signing.
 */
public class TestSecurity {
    private static final Policy POLICY = new Policy.Builder().expiry(4653651600L).build();
    private static final String APP_SECRET = "N3XOC2GP2NFTDCM43DZ6F2L6N4";
    private static final String CORRECT_ENCODED_POLICY = "eyJleHBpcnkiOjQ2NTM2NTE2MDB9";
    private static final String CORRECT_SIGNATURE = "d0dcacc68d00b7d2cd18c7f82aaf5bf172fdb423dc3cf0540d0da04912867e13";

    @Test
    public void TestEncoding() {
        Security security = Security.createNew(POLICY, APP_SECRET);

        assertTrue("Incorrect encoded policy ", CORRECT_ENCODED_POLICY.equals(security.getPolicy()));
    }

    @Test
    public void TestSigning() {
        Security security = Security.createNew(POLICY, APP_SECRET);

        assertTrue("Incorrect signature", CORRECT_SIGNATURE.equals(security.getSignature()));
    }
}
