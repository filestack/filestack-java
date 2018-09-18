package com.filestack.internal;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Holds {@link OkHttpClient} singletons.
 * We only want to instantiate these classes once per app.
 */
public final class Networking {

  private static OkHttpClient httpClient;
  private static BaseService baseService;
  private static CdnService cdnService;
  private static UploadService uploadService;
  private static CloudService cloudService;

  private static final NetworkClient networkClient = new NetworkClient(getHttpClient(), new Gson());

  /** Get http fsClient singleton. */
  public static OkHttpClient getHttpClient() {
    if (httpClient == null) {
      httpClient = new OkHttpClient.Builder()
          .addInterceptor(new HeaderInterceptor())
          .readTimeout(30, TimeUnit.SECONDS)
          .connectTimeout(30, TimeUnit.SECONDS)
          .writeTimeout(30, TimeUnit.SECONDS)
          .retryOnConnectionFailure(false)
          .build();
    }
    return httpClient;
  }

  /**
   * Get {@link BaseService} singleton.
   */
  public static BaseService getBaseService() {
    if (baseService == null) {
      baseService = new BaseService(networkClient);
    }
    return baseService;
  }

  /**
   * Get {@link CdnService} singleton.
   */
  public static CdnService getCdnService() {
    if (cdnService == null) {
      cdnService = new CdnService(networkClient);
    }
    return cdnService;
  }


  /**
   * Get {@link UploadService} singleton.
   */
  public static UploadService getUploadService() {
    if (uploadService == null) {
      uploadService = new UploadService(networkClient);
    }
    return uploadService;
  }

  /**
   * Get {@link CloudService} singleton.
   */
  public static CloudService getCloudService() {
    if (cloudService == null) {
      cloudService = new CloudService(networkClient, new Gson());
    }
    return cloudService;
  }

  private Networking() {}
}
