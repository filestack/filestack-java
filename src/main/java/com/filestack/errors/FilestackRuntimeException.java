package com.filestack.errors;

/**
 * Base {@link RuntimeException} subclass for all unchecked library exceptions.
 */
public class FilestackRuntimeException extends RuntimeException {

  public FilestackRuntimeException() {
  }

  public FilestackRuntimeException(String message) {
    super(message);
  }

  public FilestackRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public FilestackRuntimeException(Throwable cause) {
    super(cause);
  }
}
