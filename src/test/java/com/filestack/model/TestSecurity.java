package com.filestack.model;

import static com.filestack.util.MockConstants.APP_SECRET;
import static com.filestack.util.MockConstants.POLICY;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link Security Security} class to check encoding and signing.
 */
public class TestSecurity {
    private static final String CORRECT_POLICY = "eyJleHBpcnkiOjQ2NTM2NTE2MDB9";
    private static final String CORRECT_SIGNATURE =
            "d0dcacc68d00b7d2cd18c7f82aaf5bf172fdb423dc3cf0540d0da04912867e13";

    @Test
    public void testEncoding() {
        Security security = Security.createNew(POLICY, APP_SECRET);

        assertTrue("Incorrect policy ", CORRECT_POLICY.equals(security.getPolicy()));
    }

    @Test
    public void testSigning() {
        Security security = Security.createNew(POLICY, APP_SECRET);

        assertTrue("Incorrect signature", CORRECT_SIGNATURE.equals(security.getSignature()));
    }
}
