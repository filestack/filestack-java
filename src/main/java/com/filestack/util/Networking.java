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
  private static FsApiService fsApiService;
  private static FsCdnService fsCdnService;
  private static FsUploadService fsUploadService;

  /** Get http client singleton. */
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
   * Get {@link FsApiService} singleton.
   */
  public static FsApiService getFsApiService() {
    if (fsApiService == null) {
      Retrofit retrofit = getRetrofitBuilder().baseUrl(FsApiService.URL).build();
      fsApiService = retrofit.create(FsApiService.class);
    }
    return fsApiService;
  }

  /**
   * Get {@link FsCdnService} singleton.
   */
  public static FsCdnService getFsCdnService() {
    if (fsCdnService == null) {
      Retrofit retrofit = getRetrofitBuilder().baseUrl(FsCdnService.URL).build();
      fsCdnService = retrofit.create(FsCdnService.class);
    }
    return fsCdnService;
  }

  /**
   * Get {@link FsUploadService} singleton.
   */
  public static FsUploadService getFsUploadService() {
    if (fsUploadService == null) {
      Retrofit retrofit = getRetrofitBuilder().baseUrl(FsUploadService.URL).build();
      fsUploadService = retrofit.create(FsUploadService.class);
    }
    return fsUploadService;
  }

  /** Set a custom http client. */
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
    fsApiService = null;
    fsCdnService = null;
    fsUploadService = null;
  }
}
