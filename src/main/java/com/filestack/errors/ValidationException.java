package com.filestack.errors;

/** Thrown to indicate invalid object state or method argument. */
public class ValidationException extends FilestackException {

  public ValidationException() {
  }

  public ValidationException(String message) {
    super(message);
  }

  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ValidationException(Throwable cause) {
    super(cause);
  }
}
