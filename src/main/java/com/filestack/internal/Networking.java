package com.filestack.internal;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Holds {@link OkHttpClient} and {@link Retrofit} singletons.
 * We only want to instantiate these classes once per app.
 */
public final class Networking {
  private static OkHttpClient httpClient;
  private static BaseService baseService;
  private static CdnService cdnService;
  private static UploadService uploadService;
  private static CloudService cloudService;

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
      Retrofit retrofit = getRetrofitBuilder().baseUrl(BaseService.URL).build();
      baseService = retrofit.create(BaseService.class);
    }
    return baseService;
  }

  public static void setBaseService(BaseService baseService) {
    Networking.baseService = baseService;
  }

  /**
   * Get {@link CdnService} singleton.
   */
  public static CdnService getCdnService() {
    if (cdnService == null) {
      Retrofit retrofit = getRetrofitBuilder().baseUrl(CdnService.URL).build();
      cdnService = retrofit.create(CdnService.class);
    }
    return cdnService;
  }

  public static void setCdnService(CdnService cdnService) {
    Networking.cdnService = cdnService;
  }

  /**
   * Get {@link UploadService} singleton.
   */
  public static UploadService getUploadService() {
    if (uploadService == null) {
      Retrofit retrofit = getRetrofitBuilder().baseUrl(UploadService.URL).build();
      uploadService = retrofit.create(UploadService.class);
    }
    return uploadService;
  }

  public static void setUploadService(UploadService uploadService) {
    Networking.uploadService = uploadService;
  }

  /**
   * Get {@link CloudService} singleton.
   */
  public static CloudService getCloudService() {
    if (cloudService == null) {
      Retrofit retrofit = getRetrofitBuilder().baseUrl(CloudService.URL).build();
      cloudService = retrofit.create(CloudService.class);
    }
    return cloudService;
  }

  public static void setCloudService(CloudService cloudService) {
    Networking.cloudService = cloudService;
  }

  /** Sets all singletons to null so they'll be recreated. */
  public static void invalidate() {
    httpClient = null;
    invalidateServices();
  }

  private static Retrofit.Builder getRetrofitBuilder() {
    return new Retrofit.Builder()
        .client(getHttpClient())
        .addConverterFactory(GsonConverterFactory.create());
  }

  /** Sets the services to null so they'll be recreated. */
  private static void invalidateServices() {
    baseService = null;
    cdnService = null;
    uploadService = null;
    cloudService = null;
  }

  private Networking() {}
}
