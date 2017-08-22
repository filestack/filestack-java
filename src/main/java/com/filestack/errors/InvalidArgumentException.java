package com.filestack.errors;

/** Thrown to indicate a method was passed invalid arguments. */
public class InvalidArgumentException extends FilestackRuntimeException {

  public InvalidArgumentException() {
  }

  public InvalidArgumentException(String message) {
    super(message);
  }

  public InvalidArgumentException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidArgumentException(Throwable cause) {
    super(cause);
  }
}
