package com.filestack;

import com.filestack.internal.CloudServiceUtil;
import com.filestack.internal.Networking;
import com.filestack.internal.Upload;
import com.filestack.internal.Util;
import com.filestack.internal.responses.CloudStoreResponse;
import com.filestack.transforms.ImageTransform;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import retrofit2.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.concurrent.Callable;

/** Uploads new files. */
public class Client implements Serializable {
  protected final Config config;
  
  private String sessionToken;

  public Client(Config config) {
    this.config = config;
  }

  /**
   * Synchronously uploads a file system path using default storage options.
   * Wraps {@link #uploadAsync(InputStream, int, boolean, StorageOptions)}.
   *
   * @see #uploadAsync(InputStream, int, boolean, StorageOptions)
   */
  public FileLink upload(String path, boolean intel) throws IOException {
    return upload(path, intel, null);
  }

  /**
   * Synchronously uploads a file system path.
   * Wraps {@link #uploadAsync(InputStream, int, boolean, StorageOptions)}.
   *
   * @see #uploadAsync(InputStream, int, boolean, StorageOptions)
   */
  public FileLink upload(String path, boolean intel, StorageOptions opts) throws IOException {
    return uploadAsync(path, intel, opts).blockingLast().getData();
  }

  /**
   * Synchronously uploads an {@link InputStream} using default storage options.
   * Wraps {@link #uploadAsync(InputStream, int, boolean, StorageOptions)}.
   *
   * @see #uploadAsync(InputStream, int, boolean, StorageOptions)
   */
  public FileLink upload(InputStream input, int size, boolean intel) throws IOException {
    return upload(input, size, intel, null);
  }

  /**
   * Synchronously uploads an {@link InputStream}.
   * Wraps {@link #uploadAsync(InputStream, int, boolean, StorageOptions)}.
   *
   * @see #uploadAsync(InputStream, int, boolean, StorageOptions)
   */
  public FileLink upload(InputStream input, int size, boolean intel, StorageOptions opts)
      throws IOException {
    return uploadAsync(input, size, intel, opts).blockingLast().getData();
  }

  /**
   * Gets basic account info for this fsClient's API key.
   *
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   */
  public AppInfo getAppInfo() throws IOException {
    JsonObject params = CloudServiceUtil.buildBaseJson(config, null);
    Response<AppInfo> response = Networking.getCloudService().prefetch(params).execute();
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

    Util.throwIfNullOrEmpty(providerName, "Provider name is required");
    Util.throwIfNullOrEmpty(path, "Path is required");

    JsonObject params = CloudServiceUtil.buildBaseJson(config, sessionToken);
    CloudServiceUtil.addCloudJson(params, providerName, path, next);
    Response<JsonObject> response = Networking.getCloudService().list(params).execute();
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

    Util.throwIfNullOrEmpty(providerName, "Provider name is required");
    Util.throwIfNullOrEmpty(path, "Path is required");

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

    Util.throwIfNullOrEmpty(providerName, "Provider name is required");
    Util.throwIfNullOrEmpty(path, "Path is required");

    if (options == null) {
      options = new StorageOptions.Builder().build();
    }

    JsonObject params = CloudServiceUtil.buildBaseJson(config, sessionToken);
    CloudServiceUtil.addCloudJson(params, providerName, path, null);
    CloudServiceUtil.addStorageJson(params, providerName, options);
    params.add("store", options.getAsJson());
    Response<JsonObject> response = Networking.getCloudService().store(params).execute();
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

    Util.throwIfNullOrEmpty(providerName, "Provider name is required");

    JsonObject params = CloudServiceUtil.buildBaseJson(config, sessionToken);
    CloudServiceUtil.addCloudJson(params, providerName, null, null);
    Response response = Networking.getCloudService().logout(params).execute();
    Util.checkResponseAndThrow(response);
  }

  // Async methods

  /**
   * Asynchronously uploads a file system path using default storage options.
   * Wraps {@link #uploadAsync(InputStream, int, boolean, StorageOptions)}.
   *
   * @see #uploadAsync(InputStream, int, boolean, StorageOptions)
   */
  public Flowable<Progress<FileLink>> uploadAsync(String path, boolean intelligent) {
    return uploadAsync(path, intelligent, null);
  }

  /**
   * Asynchronously uploads a file system path.
   * Wraps {@link #uploadAsync(InputStream, int, boolean, StorageOptions)}.
   *
   * @see #uploadAsync(InputStream, int, boolean, StorageOptions)
   */
  public Flowable<Progress<FileLink>> uploadAsync(String path, boolean intel, StorageOptions opts) {
    try {
      File inputFile = Util.createReadFile(path);
      InputStream inputStream = new FileInputStream(inputFile);

      if (opts == null) {
        opts = new StorageOptions.Builder().filename(inputFile.getName()).build();
      } else if (Strings.isNullOrEmpty(opts.getFilename())) {
        opts = opts.newBuilder().filename(inputFile.getName()).build();
      }

      return uploadAsync(inputStream, (int) inputFile.length(), intel, opts);
    } catch (IOException e) {
      return Flowable.error(e);
    }
  }

  /**
   * Asynchronously uploads an {@link InputStream} using default storage options.
   * Wraps {@link #uploadAsync(InputStream, int, boolean, StorageOptions)}.
   *
   * @see #uploadAsync(InputStream, int, boolean, StorageOptions)
   */
  public Flowable<Progress<FileLink>> uploadAsync(InputStream input, int size, boolean intel) {
    return uploadAsync(input, size, intel, null);
  }

  /**
   * Asynchronously uploads an {@link InputStream}.
   * The returned {@link Flowable} emits a stream of {@link Progress} objects.
   * The final {@link Progress} object will return a new {@link FileLink} from the
   * {@link Progress#getData()} method.
   * The upload is not done until {@link Progress#getData()} returns non-null.
   * All exceptions, including issues opening a file, are returned through the observable.
   *
   * <p>
   * An {@link HttpException} is thrown on error response from backend.
   * An {@link IOException} is thrown on error reading file or network failure.
   * </p>
   *
   * @param intel enable intelligent ingestion, setting to true to will decrease failures in very
   *              poor network conditions at the expense of upload speed
   * @param opts  storage options, https://www.filestack.com/docs/rest-api/store
   */
  public Flowable<Progress<FileLink>> uploadAsync(
      InputStream input, int size, boolean intel, StorageOptions opts) {
    if (opts == null) {
      opts = new StorageOptions.Builder().build();
    }

    Upload upload = new Upload(config, input, size, intel, opts);
    return upload.run();
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
    });
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
    });
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
    });
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
    });
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
