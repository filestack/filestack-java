package org.filestack;

import java.io.IOException;

/**
 * {@link IOException} subclass for backend error responses.
 */
public class HttpException extends IOException {
  private int code;

  public HttpException(int code) {
    this.code = code;
  }

  public HttpException(int code, String message) {
    super(message);
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
