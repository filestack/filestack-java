package com.filestack.util;

/** Combines all REST services into a single service. */
public class FsService {
  // An instance of this class can hold individual custom services
  // It doesn't contain singletons though, so we specify these as custom
  private BaseService customApiService;
  private CdnService customCdnService;
  private UploadService customUploadService;
  private CloudService customCloudService;

  /** Constructs instance using singleton REST services. */
  public FsService() { }

  /** Constructs instance using custom REST services. */
  public FsService(BaseService api, CdnService cdn, UploadService upload,
                   CloudService cloud) {
    this.customApiService = api;
    this.customCdnService = cdn;
    this.customUploadService = upload;
    this.customCloudService = cloud;
  }

  /**
   * Returns {@link BaseService} instance, custom if provided, global singleton otherwise.
   */
  public BaseService api() {
    if (customApiService != null) {
      return customApiService;
    } else {
      return Networking.getBaseService();
    }
  }

  /**
   * Returns {@link CdnService} instance, custom if provided, global singleton otherwise.
   */
  public CdnService cdn() {
    if (customCdnService != null) {
      return customCdnService;
    } else {
      return Networking.getCdnService();
    }
  }

  /**
   * Returns {@link UploadService} instance, custom if provided, global singleton otherwise.
   */
  public UploadService upload() {
    if (customUploadService != null) {
      return customUploadService;
    } else {
      return Networking.getUploadService();
    }
  }

  /**
   * Returns {@link CloudService} instance, custom if provided, global singleton otherwise.
   */
  public CloudService cloud() {
    if (customCloudService != null) {
      return customCloudService;
    } else {
      return Networking.getCloudService();
    }
  }
}
