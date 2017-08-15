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
  private static FilestackService fsService;
  private static FilestackUploadService fsUploadService;

  /**
   * Get http client singleton.
   */
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
   * Get {@link FilestackService} singleton.
   */
  public static FilestackService getFsService() {
    if (fsService == null) {
      Retrofit retrofit = getRetrofitBuilder().baseUrl(FilestackService.URL).build();
      fsService = retrofit.create(FilestackService.class);
    }
    return fsService;
  }

  /**
   * Get {@link FilestackUploadService} singleton.
   */
  public static FilestackUploadService getFsUploadService() {
    if (fsUploadService == null) {
      Retrofit retrofit = getRetrofitBuilder().baseUrl(FilestackUploadService.URL).build();
      fsUploadService = retrofit.create(FilestackUploadService.class);
    }
    return fsUploadService;
  }

  /**
   * Set a custom http client.
   */
  public static void setCustomClient(OkHttpClient client) {
    if (client == null) {
      return;
    }
    httpClient = client;
    invalidate();
  }

  /**
   * Remove custom http client. Resets to use default client.
   */
  public static void removeCustomClient() {
    httpClient = null;
    invalidate();
  }

  private static Retrofit.Builder getRetrofitBuilder() {
    return new Retrofit.Builder()
        .client(getHttpClient())
        .addConverterFactory(GsonConverterFactory.create());
  }

  /**
   * Sets the services to null so they'll be recreated.
   */
  private static void invalidate() {
    fsService = null;
    fsUploadService = null;
  }
}
