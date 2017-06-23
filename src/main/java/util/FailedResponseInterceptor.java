package util;

import exception.FilestackIOException;
import exception.HandleNotFoundException;
import exception.PolicySignatureException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

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

        // If it was successful we don't need to do anything else
        if (response.isSuccessful())
            return response;

        // We're only concerned with responses from Filestack domains
        if (!request.url().host().contains("filestack"))
            return response;

        // Throw a descriptive subclass of FilestackIOException
        switch (response.code()) {
            case 403:
                throw new PolicySignatureException();
            case 404:
                throw new HandleNotFoundException();
            default:
                String message = String.format("Request failed: %d %s", response.code(), response.message());
                throw new FilestackIOException(message);
        }
    }
}
