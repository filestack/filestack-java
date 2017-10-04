package com.filestack.util;

import com.filestack.responses.CompleteResponse;
import com.filestack.responses.StartResponse;
import com.filestack.responses.StoreResponse;
import com.filestack.responses.UploadResponse;
import com.google.gson.JsonObject;
import java.util.Map;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/** Combines all REST services into a single service. */
public class FsService {
  // An instance of this class can hold individual custom services
  // It doesn't contain singletons though, so we specify these as custom
  private FsApiService customApiService;
  private FsCdnService customCdnService;
  private FsUploadService customUploadService;
  private FsCloudService customCloudService;

  /** Constructs instance using singleton REST services. */
  public FsService() { }

  /** Constructs instance using custom REST services. */
  public FsService(FsApiService api, FsCdnService cdn, FsUploadService upload,
                   FsCloudService cloud) {
    this.customApiService = api;
    this.customCdnService = cdn;
    this.customUploadService = upload;
    this.customCloudService = cloud;
  }

  /**
   * Returns {@link FsApiService} instance, custom if provided, global singleton otherwise.
   */
  public FsApiService api() {
    if (customApiService != null) {
      return customApiService;
    } else {
      return Networking.getFsApiService();
    }
  }

  /**
   * Returns {@link FsCdnService} instance, custom if provided, global singleton otherwise.
   */
  public FsCdnService cdn() {
    if (customCdnService != null) {
      return customCdnService;
    } else {
      return Networking.getFsCdnService();
    }
  }

  /**
   * Returns {@link FsUploadService} instance, custom if provided, global singleton otherwise.
   */
  public FsUploadService upload() {
    if (customUploadService != null) {
      return customUploadService;
    } else {
      return Networking.getFsUploadService();
    }
  }

  /**
   * Returns {@link FsCloudService} instance, custom if provided, global singleton otherwise.
   */
  public FsCloudService cloud() {
    if (customCloudService != null) {
      return customCloudService;
    } else {
      return Networking.getFsCloudService();
    }
  }
}
