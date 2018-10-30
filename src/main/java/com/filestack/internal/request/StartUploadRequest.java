package com.filestack.internal.request;

public class StartUploadRequest {
  private final BaseUploadParams baseUploadParams;

  public StartUploadRequest(BaseUploadParams baseUploadParams) {
    this.baseUploadParams = baseUploadParams;
  }

  public BaseUploadParams getBaseUploadParams() {
    return baseUploadParams;
  }
}
