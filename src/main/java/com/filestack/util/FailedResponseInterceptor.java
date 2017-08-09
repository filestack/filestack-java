package com.filestack.util;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

/**
 * Checks for failed responses from Filestack and throws an exception rather than returning the response.
 * Makes error handling simpler by centralizing this logic in one place.
 */
public class FailedResponseInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        ResponseBody body = response.body();

        // If it was successful we don't need to do anything else
        if (response.isSuccessful())
            return response;

        if (body != null)
            throw new FilestackIOException(body.string());
        else
            throw new FilestackIOException(request.url().host() + " " + response.code() + " " + response.message());
    }
}
