package com.filestack.internal.request;

import javax.annotation.Nullable;

public class UploadRequest {

  private final String apiKey;
  private final int part;
  private final long size;
  private final String md5;
  private final String uri;
  private final String region;
  private final String uploadId;
  private final boolean isIntelligentIngestion;
  @Nullable
  private final Long offset;

  public UploadRequest(String apiKey, int part, long size, String md5, String uri, String region, String uploadId,
                       boolean isIntelligentIngestion, @Nullable Long offset) {
    this.apiKey = apiKey;
    this.part = part;
    this.size = size;
    this.md5 = md5;
    this.uri = uri;
    this.region = region;
    this.uploadId = uploadId;
    this.isIntelligentIngestion = isIntelligentIngestion;
    this.offset = offset;
  }

  public String getApiKey() {
    return apiKey;
  }

  public int getPart() {
    return part;
  }

  public long getSize() {
    return size;
  }

  public String getMd5() {
    return md5;
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

  public boolean isIntelligentIngestion() {
    return isIntelligentIngestion;
  }

  @Nullable
  public Long getOffset() {
    return offset;
  }
}
