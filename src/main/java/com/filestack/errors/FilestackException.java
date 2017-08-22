package com.filestack.errors;

/**
 * Base {@link Exception} subclass for all checked library exceptions.
 */
public class FilestackException extends Exception {

  public FilestackException() {
  }

  public FilestackException(String message) {
    super(message);
  }

  public FilestackException(String message, Throwable cause) {
    super(message, cause);
  }

  public FilestackException(Throwable cause) {
    super(cause);
  }
}
