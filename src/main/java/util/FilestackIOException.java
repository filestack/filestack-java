package util;

import java.io.IOException;

/**
 * Base class for Filestack IO, networking, and API response exceptions.
 */
public class FilestackIOException extends IOException {

    public FilestackIOException() {
    }

    public FilestackIOException(String message) {
        super(message);
    }

    public FilestackIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilestackIOException(Throwable cause) {
        super(cause);
    }
}
