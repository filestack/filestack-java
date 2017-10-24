package com.filestack;

import com.filestack.transforms.ImageTransform;
import com.filestack.util.FsService;
import com.filestack.util.Upload;
import com.filestack.util.Util;
import com.filestack.util.responses.CloudStoreResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.concurrent.Callable;
import retrofit2.Response;

/** Uploads new files. */
public class FsClient {
  protected final FsService fsService;
  protected final Scheduler subScheduler;
  protected final Scheduler obsScheduler;
  protected final Security security;
  protected final String apiKey;
  protected final String returnUrl;
  
  private String sessionToken;

  /**
   * Builds new {@link FsFile}.
   */
  public static class Builder {
    private FsService fsService;
    private Scheduler subScheduler;
    private Scheduler obsScheduler;
    private Security security;
    private String apiKey;
    private String sessionToken;
    private String returnUrl;

    public Builder fsService(FsService fsService) {
      this.fsService = fsService;
      return this;
    }

    public Builder subScheduler(Scheduler subScheduler) {
      this.subScheduler = subScheduler;
      return this;
    }

    public Builder obsScheduler(Scheduler obsScheduler) {
      this.obsScheduler = obsScheduler;
      return this;
    }

    public Builder security(Security security) {
      this.security = security;
      return this;
    }

    public Builder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public Builder sessionToken(String sessionToken) {
      this.sessionToken = sessionToken;
      return this;
    }

    public Builder returnUrl(String returnUrl) {
      this.returnUrl = returnUrl;
      return this;
    }

    /**
     * Create the {@link FsFile} using the configured values.
     */
    public FsClient build() {
      return new FsClient(this);
    }
  }

  protected FsClient(Builder builder) {
    fsService = builder.fsService != null ? builder.fsService : new FsService();
    subScheduler = builder.subScheduler != null ? builder.subScheduler : Schedulers.io();
    obsScheduler = builder.obsScheduler != null ? builder.obsScheduler : Schedulers.single();
    security = builder.security;
    apiKey = builder.apiKey;
    sessionToken = builder.sessionToken;
    returnUrl = builder.returnUrl;
  }

  /**
   * Uploads local file using default storage options.
   *
   * @see #upload(String, boolean, StorageOptions)
   */
  public FsFile upload(String path, boolean intelligent) throws IOException {
    return upload(path, intelligent, null);
  }

  /**
   * Uploads local file.
   *
   * @param path        path to the file, can be local or absolute
   * @param options     storage options, https://www.filestack.com/docs/rest-api/store
   * @param intelligent intelligent ingestion, setting to true to will decrease failures in very
   *                    poor network conditions at the expense of upload speed
   * @return new {@link FsFile} referencing file
   * @throws HttpException on error response from backend
   * @throws IOException           on error reading file or network failure
   */
  public FsFile upload(String path, boolean intelligent, StorageOptions options)
      throws IOException {

    try {
      return uploadAsync(path, intelligent, options).blockingLast().getData();
    } catch (RuntimeException e) {
      throw (IOException) e.getCause();
    }
  }

  /**
   * Gets basic account info for this fsClient's API key.
   *
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   */
  public AppInfo getAppInfo() throws IOException {
    JsonObject params = makeCloudParams();
    Response<AppInfo> response = fsService.cloud().prefetch(params).execute();
    Util.checkResponseAndThrow(response);
    return response.body();
  }

  /**
   * Gets contents of a user's cloud "drive".
   *
   * @see #getCloudItems(String, String, String)
   */
  public CloudResponse getCloudItems(String providerName, String path) throws IOException {
    return getCloudItems(providerName, path, null);
  }

  /**
   * Gets contents of a user's cloud "drive". If the user has not authorized for the provider, the
   * response will contain an OAuth URL that should be opened in a browser.
   *
   * @param providerName one of the static CLOUD constants in this class
   * @param next         pagination token returned in previous response
   *
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   */
  public CloudResponse getCloudItems(String providerName, String path, String next)
      throws IOException {

    JsonObject params = makeCloudParams(providerName, path, next);
    Response<JsonObject> response = fsService.cloud().list(params).execute();
    Util.checkResponseAndThrow(response);
    JsonObject base = response.body();

    if (base.has("token")) {
      sessionToken = base.get("token").getAsString();
    }

    JsonElement provider = base.get(providerName);
    Gson gson = new Gson();
    return gson.fromJson(provider, CloudResponse.class);
  }

  /**
   * Transfers file from a user's cloud "drive" to Filestack. Uses default storage options.
   *
   * @see #storeCloudItem(String, String, StorageOptions)
   */
  public FsFile storeCloudItem(String providerName, String path) throws IOException {
    return storeCloudItem(providerName, path, null);
  }

  /**
   * Transfers file from a user's cloud "drive" to Filestack.
   *
   * @param providerName one of the static CLOUD constants in this class
   * @param options      storage options for how to save the file in Filestack
   * @return             new filelink
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   */
  public FsFile storeCloudItem(String providerName, String path, StorageOptions options)
      throws IOException {

    if (options == null) {
      options = new StorageOptions();
    }

    JsonObject params = makeCloudParams(providerName, path);
    params.add("store", options.getAsJson());
    Response<JsonObject> response = fsService.cloud().store(params).execute();
    Util.checkResponseAndThrow(response);
    JsonElement responseJson = response.body().get(providerName);
    Gson gson = new Gson();
    CloudStoreResponse storeInfo = gson.fromJson(responseJson, CloudStoreResponse.class);
    return new FsFile(this, storeInfo.getHandle());
  }

  /**
   * Logs out from specified cloud.
   *
   * @param providerName one of the static CLOUD constants in this class
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   */
  public void logoutCloud(String providerName) throws IOException {
    JsonObject params = makeCloudParams(providerName, "/");
    Response response = fsService.cloud().logout(params).execute();
    Util.checkResponseAndThrow(response);
  }

  // Async methods

  /**
   * Asynchronously uploads local file using default storage options.
   *
   * @see #upload(String, boolean, StorageOptions)
   * @see #uploadAsync(String, boolean, StorageOptions)
   */
  public Flowable<Progress<FsFile>> uploadAsync(String path, boolean intelligent) {
    return uploadAsync(path, intelligent, null);
  }

  /**
   * Asynchronously uploads local file. A stream of {@link Progress} objects are emitted by the
   * returned {@link Flowable}. The final {@link Progress} object will return a new
   * {@link FsFile} from {@link Progress#getData()}. The upload is not done until
   * {@link Progress#getData()} returns non-null.
   *
   * @see #upload(String, boolean, StorageOptions)
   */
  public Flowable<Progress<FsFile>> uploadAsync(String path, boolean intelligent,
                                                StorageOptions options) {

    if (options == null) {
      options = new StorageOptions();
    }

    if (!options.hasContentType()) {
      String contentType = guessContentType(path);
      options = options.newBuilder().contentType(contentType).build();
    }

    Upload upload = new Upload(this, path, intelligent, options);
    return upload.runAsync();
  }

  /**
   * Asynchronously get basic account info for this fsClient's API key.
   *
   * @see #getAppInfo()
   */
  public Single<AppInfo> getAppInfoAsync() {
    return Single.fromCallable(new Callable<AppInfo>() {
      @Override
      public AppInfo call() throws Exception {
        return getAppInfo();
      }
    })
        .subscribeOn(subScheduler)
        .observeOn(obsScheduler);
  }

  /**
   * Asynchronously gets contents of a user's cloud "drive".
   *
   * @see #getCloudItems(String, String, String)
   */
  public Single<CloudResponse> getCloudItemsAsync(String providerName, String path) {
    return getCloudItemsAsync(providerName, path, null);
  }

  /**
   * Asynchronously gets contents of a user's cloud "drive".
   *
   * @see #getCloudItems(String, String, String)
   */
  public Single<CloudResponse> getCloudItemsAsync(final String providerName, final String path,
                                                     final String next) {

    return Single.fromCallable(new Callable<CloudResponse>() {
      @Override
      public CloudResponse call() throws Exception {
        return getCloudItems(providerName, path, next);
      }
    })
        .subscribeOn(subScheduler)
        .observeOn(obsScheduler);
  }

  /**
   * Asynchronously transfers file from a user's cloud "drive" to Filestack.
   * Uses default storage options.
   *
   * @see #storeCloudItem(String, String, StorageOptions)
   */
  public Single<FsFile> storeCloudItemAsync(final String providerName, final String path) {
    return storeCloudItemAsync(providerName, path, null);
  }

  /**
   * Asynchronously transfers file from a user's cloud "drive" to Filestack.
   *
   * @see #storeCloudItem(String, String, StorageOptions)
   */
  public Single<FsFile> storeCloudItemAsync(final String providerName, final String path,
                                            final StorageOptions options) {

    return Single.fromCallable(new Callable<FsFile>() {
      @Override
      public FsFile call() throws Exception {
        return storeCloudItem(providerName, path, options);
      }
    })
        .subscribeOn(subScheduler)
        .observeOn(obsScheduler);
  }

  /**
   * Asynchronously logs out from specified cloud.
   *
   * @see #logoutCloud(String)
   */
  public Completable logoutCloudAsync(final String providerName) {
    return Completable.fromAction(new Action() {
      @Override
      public void run() throws Exception {
        logoutCloud(providerName);
      }
    })
        .subscribeOn(subScheduler)
        .observeOn(obsScheduler);
  }

  /**
   * Creates an {@link ImageTransform} object for this file. A transformation call isn't made
   * directly by this method.
   *
   * @return {@link ImageTransform ImageTransform} instance configured for this file
   */
  public ImageTransform imageTransform(String url) {
    return new ImageTransform(this, url);
  }

  protected static String guessContentType(String path) {
    return "application/octet-stream";
  }

  /**
   * Creates a {@link JsonObject} with this fsClient's config.
   */
  protected JsonObject makeCloudParams() {
    JsonObject json = new JsonObject();
    json.addProperty("apikey", apiKey);
    if (security != null) {
      json.addProperty("policy", security.getPolicy());
      json.addProperty("signature", security.getSignature());
    }
    json.addProperty("flow", "mobile");
    json.addProperty("appurl", returnUrl);
    if (sessionToken != null) {
      json.addProperty("token", sessionToken);
    }
    return json;
  }

  /**
   * Creates a {@link JsonObject} with this fsClient's config. Adds provider info.
   */
  protected JsonObject makeCloudParams(String providerName, String path) {
    return makeCloudParams(providerName, path, null);
  }

  /**
   * Creates a {@link JsonObject} with this fsClient's config. Adds provider info with next token.
   */
  protected JsonObject makeCloudParams(String providerName, String path, String next) {
    JsonObject provider = new JsonObject();
    provider.addProperty("path", path);
    if (next != null) {
      provider.addProperty("next", next);
    }
    JsonObject clouds = new JsonObject();
    clouds.add(providerName, provider);
    JsonObject base = makeCloudParams();
    base.add("clouds", clouds);
    return base;
  }

  public FsService getFsService() {
    return fsService;
  }

  public Scheduler getSubScheduler() {
    return subScheduler;
  }

  public Scheduler getObsScheduler() {
    return obsScheduler;
  }

  public String getApiKey() {
    return apiKey;
  }

  public Security getSecurity() {
    return security;
  }

  public String getSessionToken() {
    return sessionToken;
  }

  public String getReturnUrl() {
    return returnUrl;
  }

  public void setSessionToken(String sessionToken) {
    this.sessionToken = sessionToken;
  }
}
