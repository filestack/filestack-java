package com.filestack;

import java.io.IOException;

/**
 * {@link IOException} subclass for backend error responses.
 */
public class HttpResponseException extends IOException {
  private int code;

  public HttpResponseException(int code) {
    this.code = code;
  }

  public HttpResponseException(int code, String message) {
    super(message);
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
