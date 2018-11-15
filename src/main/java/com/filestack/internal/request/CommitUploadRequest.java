package com.filestack.internal.request;

public class CommitUploadRequest {
  private final String apiKey;
  private final long size;
  private final int part;
  private final String uri;
  private final String region;
  private final String uploadId;
  private final String storeLocation;

  public CommitUploadRequest(String apiKey, long size, int part, String uri, String region, String uploadId,
                             String storeLocation) {
    this.apiKey = apiKey;
    this.size = size;
    this.part = part;
    this.uri = uri;
    this.region = region;
    this.uploadId = uploadId;
    this.storeLocation = storeLocation;
  }

  public String getApiKey() {
    return apiKey;
  }

  public long getSize() {
    return size;
  }

  public int getPart() {
    return part;
  }

  public String getUri() {
    return uri;
  }

  public String getRegion() {
    return region;
  }

  public String getUploadId() {
    return uploadId;
  }

  public String getStoreLocation() {
    return storeLocation;
  }
}
