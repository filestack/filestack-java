package com.filestack;

import com.filestack.transforms.ImageTransform;
import com.filestack.util.Upload;
import com.filestack.util.Util;
import com.filestack.util.responses.CloudStoreResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import retrofit2.Response;

import java.io.IOException;
import java.util.concurrent.Callable;

/** Uploads new files. */
public class Client {
  protected final Config config;
  protected final String returnUrl;
  
  private String sessionToken;

  public Client(Config config) {
    this.config = config;
    this.returnUrl = null;
  }
  
  public Client(Config config, String returnUrl) {
    this.config = config;
    this.returnUrl = null;
  }

  /**
   * Uploads local file using default storage options.
   *
   * @see #upload(String, boolean, StorageOptions)
   */
  public FileLink upload(String path, boolean intelligent) throws IOException {
    return upload(path, intelligent, null);
  }

  /**
   * Uploads local file.
   *
   * @param path        path to the file, can be local or absolute
   * @param options     storage options, https://www.filestack.com/docs/rest-api/store
   * @param intelligent intelligent ingestion, setting to true to will decrease failures in very
   *                    poor network conditions at the expense of upload speed
   * @return new {@link FileLink} referencing file
   * @throws HttpException on error response from backend
   * @throws IOException           on error reading file or network failure
   */
  public FileLink upload(String path, boolean intelligent, StorageOptions options)
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
    Response<AppInfo> response = config.getCloudService().prefetch(params).execute();
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
  @SuppressWarnings("ConstantConditions")
  public CloudResponse getCloudItems(String providerName, String path, String next)
      throws IOException {

    JsonObject params = makeCloudParams(providerName, path, next);
    Response<JsonObject> response = config.getCloudService().list(params).execute();
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
  public FileLink storeCloudItem(String providerName, String path) throws IOException {
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
  @SuppressWarnings("ConstantConditions")
  public FileLink storeCloudItem(String providerName, String path, StorageOptions options)
      throws IOException {

    if (options == null) {
      options = new StorageOptions();
    }

    JsonObject params = makeCloudParams(providerName, path);
    params.add("store", options.getAsJson());
    Response<JsonObject> response = config.getCloudService().store(params).execute();
    Util.checkResponseAndThrow(response);
    JsonElement responseJson = response.body().get(providerName);
    Gson gson = new Gson();
    CloudStoreResponse storeInfo = gson.fromJson(responseJson, CloudStoreResponse.class);
    return new FileLink(config, storeInfo.getHandle());
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
    Response response = config.getCloudService().logout(params).execute();
    Util.checkResponseAndThrow(response);
  }

  // Async methods

  /**
   * Asynchronously uploads local file using default storage options.
   *
   * @see #upload(String, boolean, StorageOptions)
   * @see #uploadAsync(String, boolean, StorageOptions)
   */
  public Flowable<Progress<FileLink>> uploadAsync(String path, boolean intelligent) {
    return uploadAsync(path, intelligent, null);
  }

  /**
   * Asynchronously uploads local file. A stream of {@link Progress} objects are emitted by the
   * returned {@link Flowable}. The final {@link Progress} object will return a new
   * {@link FileLink} from {@link Progress#getData()}. The upload is not done until
   * {@link Progress#getData()} returns non-null.
   *
   * @see #upload(String, boolean, StorageOptions)
   */
  public Flowable<Progress<FileLink>> uploadAsync(String path, boolean intelligent,
                                                  StorageOptions options) {

    if (options == null) {
      options = new StorageOptions();
    }

    if (!options.hasContentType()) {
      String contentType = guessContentType(path);
      options = options.newBuilder().contentType(contentType).build();
    }

    Upload upload = new Upload(config, path, intelligent, options);
    return upload.runAsync()
        .subscribeOn(config.getSubScheduler())
        .observeOn(config.getObsScheduler());
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
        .subscribeOn(config.getSubScheduler())
        .observeOn(config.getObsScheduler());
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
        .subscribeOn(config.getSubScheduler())
        .observeOn(config.getObsScheduler());
  }

  /**
   * Asynchronously transfers file from a user's cloud "drive" to Filestack.
   * Uses default storage options.
   *
   * @see #storeCloudItem(String, String, StorageOptions)
   */
  public Single<FileLink> storeCloudItemAsync(final String providerName, final String path) {
    return storeCloudItemAsync(providerName, path, null);
  }

  /**
   * Asynchronously transfers file from a user's cloud "drive" to Filestack.
   *
   * @see #storeCloudItem(String, String, StorageOptions)
   */
  public Single<FileLink> storeCloudItemAsync(final String providerName, final String path,
                                              final StorageOptions options) {

    return Single.fromCallable(new Callable<FileLink>() {
      @Override
      public FileLink call() throws Exception {
        return storeCloudItem(providerName, path, options);
      }
    })
        .subscribeOn(config.getSubScheduler())
        .observeOn(config.getObsScheduler());
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
        .subscribeOn(config.getSubScheduler())
        .observeOn(config.getObsScheduler());
  }

  /**
   * Creates an {@link ImageTransform} object for this file. A transformation call isn't made
   * directly by this method.
   *
   * @return {@link ImageTransform ImageTransform} instance configured for this file
   */
  public ImageTransform imageTransform(String url) {
    return new ImageTransform(config, url, true);
  }

  protected static String guessContentType(String path) {
    return "application/octet-stream";
  }

  /**
   * Creates a {@link JsonObject} with this fsClient's config.
   */
  protected JsonObject makeCloudParams() {
    JsonObject json = new JsonObject();
    json.addProperty("apikey", config.getApiKey());
    if (config.hasSecurity()) {
      json.addProperty("policy", config.getPolicy());
      json.addProperty("signature", config.getSignature());
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

  public Config getConfig() {
    return config;
  }

  public String getSessionToken() {
    return sessionToken;
  }

  public void setSessionToken(String sessionToken) {
    this.sessionToken = sessionToken;
  }
}
