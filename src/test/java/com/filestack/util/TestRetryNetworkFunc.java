package com.filestack.util;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import retrofit2.Response;

public class TestRetryNetworkFunc {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testNetworkFailure() throws IOException {
        RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(5, 5, 0) {

            @Override
            Response work() throws IOException {
                throw new IOException();
            }
        };

        thrown.expect(IOException.class);
        thrown.expectMessage("Upload failed: Network unusable");
        retryNetworkFunc.call();

        Assert.assertEquals("Expected 5 network retries", 5, retryNetworkFunc.getNetworkRetries());
    }

    @Test
    public void testServerFailure() throws IOException {
        RetryNetworkFunc retryNetworkFunc = new RetryNetworkFunc<Void>(5, 5, 0) {

            @Override
            Response work() throws IOException {
                MediaType mediaType = MediaType.parse("text/plain");
                return Response.error(500, ResponseBody.create(mediaType, "test"));
            }
        };

        thrown.expect(IOException.class);
        thrown.expectMessage("Upload failed: 500");
        retryNetworkFunc.call();

        Assert.assertEquals("Expected 5 server retries", 5, retryNetworkFunc.getServerRetries());
    }
}
