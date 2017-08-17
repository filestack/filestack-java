package com.filestack.errors;

/** Thrown to indicate a request had invalid or missing parameters. */
public class InvalidParameterException extends FilestackException {

  public InvalidParameterException() {
  }

  public InvalidParameterException(String message) {
    super(message);
  }

  public InvalidParameterException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidParameterException(Throwable cause) {
    super(cause);
  }
}
