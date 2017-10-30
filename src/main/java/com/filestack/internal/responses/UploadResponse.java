package com.filestack.internal.responses;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/** Response object from multipart upload endpoint. */
public class UploadResponse {
  private String url;
  @SerializedName("location_url")
  private String locationUrl;
  @SerializedName("headers")
  private S3Headers s3Headers;

  public String getUrl() {
    return url;
  }

  public String getLocationUrl() {
    return locationUrl;
  }

  /**
   * Return {@link Map} of S3 headers.
   */
  public Map<String, String> getS3Headers() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Authorization", s3Headers.auth);
    if (s3Headers.acl != null) {
      headers.put("x-amz-acl", s3Headers.acl);
    }
    headers.put("Content-MD5", s3Headers.md5);
    headers.put("x-amz-content-sha256", s3Headers.sha256);
    headers.put("x-amz-date", s3Headers.date);

    return headers;
  }

  private class S3Headers {
    @SerializedName("Authorization")
    private String auth;
    @SerializedName("x-amz-acl")
    private String acl;
    @SerializedName("Content-MD5")
    private String md5;
    @SerializedName("x-amz-content-sha256")
    private String sha256;
    @SerializedName("x-amz-date")
    private String date;
  }
}
