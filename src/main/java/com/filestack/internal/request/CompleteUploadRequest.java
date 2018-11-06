package com.filestack.internal.request;

public class CompleteUploadRequest {

  public static CompleteUploadRequest withIntelligentIngestion(String apiKey, String uri, String region, String uploadId, String filename, long size, String mimeType, String storeLocation, String storeRegion, String storeContainer, String storePath, String storeAccess) {
    return new CompleteUploadRequest(apiKey, uri, region, uploadId, filename, size, mimeType, storeLocation, storeRegion, storeContainer, storePath, storeAccess, true, 0);
  }


  public static CompleteUploadRequest regular(String apiKey, String uri, String region, String uploadId, String filename, long size, String mimeType, String storeLocation, String storeRegion, String storeContainer, String storePath, String storeAccess, int parts) {
    return new CompleteUploadRequest(apiKey, uri, region, uploadId, filename, size, mimeType, storeLocation, storeRegion, storeContainer, storePath, storeAccess, false, parts);
  }

  private final String apiKey;
  private final String uri;
  private final String region;
  private final String uploadId;
  private final String filename;
  private final long size;
  private final String mimeType;
  private final String storeLocation;
  private final String storeRegion;
  private final String storeContainer;
  private final String storePath;
  private final String storeAccess;
  private final boolean intelligentIngestion;
  private final int parts;

  private CompleteUploadRequest(String apiKey, String uri, String region, String uploadId, String filename, long size, String mimeType, String storeLocation, String storeRegion, String storeContainer, String storePath, String storeAccess, boolean intelligentIngestion, int parts) {
    this.apiKey = apiKey;
    this.uri = uri;
    this.region = region;
    this.uploadId = uploadId;
    this.filename = filename;
    this.size = size;
    this.mimeType = mimeType;
    this.storeLocation = storeLocation;
    this.storeRegion = storeRegion;
    this.storeContainer = storeContainer;
    this.storePath = storePath;
    this.storeAccess = storeAccess;
    this.intelligentIngestion = intelligentIngestion;
    this.parts = parts;
  }

  public String getApiKey() {
    return apiKey;
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

  public String getFilename() {
    return filename;
  }

  public long getSize() {
    return size;
  }

  public String getMimeType() {
    return mimeType;
  }

  public String getStoreLocation() {
    return storeLocation;
  }

  public String getStoreRegion() {
    return storeRegion;
  }

  public String getStoreContainer() {
    return storeContainer;
  }

  public String getStorePath() {
    return storePath;
  }

  public String getStoreAccess() {
    return storeAccess;
  }

  public boolean isIntelligentIngestion() {
    return intelligentIngestion;
  }

  public int getParts() {
    return parts;
  }
}
