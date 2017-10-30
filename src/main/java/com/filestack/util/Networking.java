package com.filestack.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Holds {@link OkHttpClient} and {@link Retrofit} singletons.
 * We only want to instantiate these classes once per app.
 */
public class Networking {
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

  /** Set a custom http fsClient. */
  public static void setCustomClient(OkHttpClient client) {
    if (client == null) {
      return;
    }
    httpClient = client;
    invalidateServices();
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
}
