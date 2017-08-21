package com.filestack.errors;

/** Thrown to indicate a request had an issue with its policy or signature. */
public class PolicySignatureException extends FilestackException {

  public PolicySignatureException() {
  }

  public PolicySignatureException(String message) {
    super(message);
  }

  public PolicySignatureException(String message, Throwable cause) {
    super(message, cause);
  }

  public PolicySignatureException(Throwable cause) {
    super(cause);
  }
}
