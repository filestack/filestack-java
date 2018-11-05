package com.filestack.internal.request;

import com.filestack.StorageOptions;

import javax.annotation.Nullable;

public class StartUploadRequest {
  private final String apiKey;
  private final long size;
  private final boolean intelligentIngestion;
  private final String policy;
  private final String signature;
  @Nullable
  private final String filename;
  @Nullable
  private final String mimeType;
  @Nullable
  private final String storeLocation;
  private final String storeRegion;
  private final String storeContainer;
  private final String storePath;
  private final String storeAccess;

  StartUploadRequest(String apiKey, long size, boolean intelligentIngestion, String policy, String signature,
                            String filename, String mimeType, String storeLocation, String storeRegion,
                            String storeContainer, String storePath, String storeAccess) {
    this.apiKey = apiKey;
    this.size = size;
    this.intelligentIngestion = intelligentIngestion;
    this.policy = policy;
    this.signature = signature;
    this.filename = filename;
    this.mimeType = mimeType;
    this.storeLocation = storeLocation;
    this.storeRegion = storeRegion;
    this.storeContainer = storeContainer;
    this.storePath = storePath;
    this.storeAccess = storeAccess;
  }

  public StartUploadRequest(String apiKey, long size, boolean intelligentIngestion, String policy, String signature,
                     StorageOptions storageOptions) {
    this.apiKey = apiKey;
    this.size = size;
    this.intelligentIngestion = intelligentIngestion;
    this.policy = policy;
    this.signature = signature;
    this.filename = storageOptions.getFilename();
    this.mimeType = storageOptions.getMimeType();
    this.storeLocation = storageOptions.getLocation();
    this.storeRegion = storageOptions.getRegion();
    this.storeContainer = storageOptions.getContainer();
    this.storePath = storageOptions.getPath();
    this.storeAccess = storageOptions.getAccess();
  }

  public String getApiKey() {
    return apiKey;
  }

  public long getSize() {
    return size;
  }

  public boolean isIntelligentIngestion() {
    return intelligentIngestion;
  }

  public String getPolicy() {
    return policy;
  }

  public String getSignature() {
    return signature;
  }

  @Nullable
  public String getFilename() {
    return filename;
  }

  @Nullable
  public String getMimeType() {
    return mimeType;
  }

  @Nullable
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
}
