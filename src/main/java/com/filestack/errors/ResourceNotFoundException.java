package com.filestack.errors;

/** Thrown when a resource (file handle, API Key, URL, etc) was not found. */
public class ResourceNotFoundException extends FilestackException {

  public ResourceNotFoundException() {
  }

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResourceNotFoundException(Throwable cause) {
    super(cause);
  }
}
