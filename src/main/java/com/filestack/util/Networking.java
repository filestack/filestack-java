package com.filestack.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Serves as singleton holder for the OkHttp client and Retrofit services.
 * We only want to instantiate these classes once per app.
 */
public class Networking {
  private static OkHttpClient httpClient;
  private static FilestackService.Cdn cdnService;
  private static FilestackService.Api apiService;
  private static FilestackService.Process processService;
  private static FilestackService.Upload uploadService;

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
   * Get CDN service singleton.
   */
  public static FilestackService.Cdn getCdnService() {
    if (cdnService == null) {
      Retrofit retrofit = getRetrofitBuilder().baseUrl(FilestackService.Cdn.URL).build();
      cdnService = retrofit.create(FilestackService.Cdn.class);
    }
    return cdnService;
  }

  /**
   * Get API service singleton.
   */
  public static FilestackService.Api getApiService() {
    if (apiService == null) {
      Retrofit retrofit = getRetrofitBuilder().baseUrl(FilestackService.Api.URL).build();
      apiService = retrofit.create(FilestackService.Api.class);
    }
    return apiService;
  }

  /**
   * Get process service singleton.
   */
  public static FilestackService.Process getProcessService() {
    if (processService == null) {
      Retrofit retrofit = getRetrofitBuilder().baseUrl(FilestackService.Process.URL).build();
      processService = retrofit.create(FilestackService.Process.class);
    }
    return processService;
  }

  /**
   * Get upload service singleton.
   */
  public static FilestackService.Upload getUploadService() {
    if (uploadService == null) {
      Retrofit retrofit = getRetrofitBuilder().baseUrl(FilestackService.Upload.URL).build();
      uploadService = retrofit.create(FilestackService.Upload.class);
    }
    return uploadService;
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
    cdnService = null;
    apiService = null;
    processService = null;
    uploadService = null;
  }
}
