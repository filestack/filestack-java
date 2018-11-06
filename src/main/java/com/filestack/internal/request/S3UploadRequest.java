package com.filestack.internal.request;

import okhttp3.HttpUrl;

import java.util.Map;

public class S3UploadRequest {
  private final HttpUrl url;
  private final Map<String, String> headers;
  private final String mimeType;
  private final byte[] data;
  private final int offset;
  private final int byteCount;

  public S3UploadRequest(HttpUrl url, Map<String, String> headers, String mimeType, byte[] data, int offset, int byteCount) {
    this.url = url;
    this.headers = headers;
    this.mimeType = mimeType;
    this.data = data;
    this.offset = offset;
    this.byteCount = byteCount;
  }

  public HttpUrl getUrl() {
    return url;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getMimeType() {
    return mimeType;
  }

  public byte[] getData() {
    return data;
  }

  public int getOffset() {
    return offset;
  }

  public int getByteCount() {
    return byteCount;
  }
}
