package com.filestack.errors;

/** Thrown when an unexpected error occurs in the library or Filestack service. */
public class InternalException extends FilestackException {

  public InternalException() {
  }

  public InternalException(String message) {
    super(message);
  }

  public InternalException(String message, Throwable cause) {
    super(message, cause);
  }

  public InternalException(Throwable cause) {
    super(cause);
  }
}
