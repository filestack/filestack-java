package com.filestack.util;

import java.io.IOException;

/**
 * Base class for Filestack IO, networking, and API response exceptions.
 */
public class FilestackException extends IOException {

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
