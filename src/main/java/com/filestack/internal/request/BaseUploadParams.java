package com.filestack.internal.request;

import com.filestack.StorageOptions;

import javax.annotation.Nullable;

public class BaseUploadParams {
  private final String apiKey;
  private final long size;
  private final StorageOptions storageOptions;
  @Nullable
  private final String multipart;
  @Nullable
  private final String policy;
  @Nullable
  private final String signature;

  public BaseUploadParams(String apiKey, long size, StorageOptions storageOptions, @Nullable String multipart, @Nullable String policy, @Nullable String signature) {
    this.apiKey = apiKey;
    this.size = size;
    this.storageOptions = storageOptions;
    this.multipart = multipart;
    this.policy = policy;
    this.signature = signature;
  }

  public String getApiKey() {
    return apiKey;
  }

  public long getSize() {
    return size;
  }

  public StorageOptions getStorageOptions() {
    return storageOptions;
  }

  @Nullable
  public String getMultipart() {
    return multipart;
  }

  @Nullable
  public String getPolicy() {
    return policy;
  }

  @Nullable
  public String getSignature() {
    return signature;
  }
}
