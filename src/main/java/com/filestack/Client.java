package org.filestack;

import org.filestack.internal.BaseService;
import org.filestack.internal.CdnService;
import org.filestack.internal.CloudService;
import org.filestack.internal.CloudServiceUtil;
import org.filestack.internal.Networking;
import org.filestack.internal.Response;
import org.filestack.internal.Upload;
import org.filestack.internal.UploadService;
import org.filestack.internal.Util;
import org.filestack.internal.responses.CloudStoreResponse;
import org.filestack.transforms.ImageTransform;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import okhttp3.ResponseBody;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.concurrent.Callable;

/** Uploads new files. */
public class Client implements Serializable {

  private final CdnService cdnService;
  private final UploadService uploadService;
  private final BaseService baseService;
  private final CloudService cloudService;
  protected final Config config;
  
  private String sessionToken;

  /**
   * Basic constructor for Client class.
   * @param config - configuration for this Client's instance
   */
  public Client(Config config) {
    this.config = config;
    this.cdnService = Networking.getCdnService();
    this.uploadService = Networking.getUploadService();
    this.baseService = Networking.getBaseService();
    this.cloudService = Networking.getCloudService();
  }

  Client(Config config, CdnService cdnService, BaseService baseService, UploadService uploadService,
         CloudService cloudService) {
    this.config = config;
    this.cdnService = cdnService;
    this.uploadService = uploadService;
    this.baseService = baseService;
    this.cloudService = cloudService;
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
  public FileLink upload(String path, boolean intel, @Nullable StorageOptions opts) throws IOException {
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
  public FileLink upload(InputStream input, int size, boolean intel, @Nullable StorageOptions opts)
      throws IOException {
    return uploadAsync(input, size, intel, opts).blockingLast().getData();
  }

  /**
   * Acquires a reference to a {@link FileLink} based on an unique handle.
   * @param handle - a unique reference to a file uploaded through our API
   */
  public FileLink fileLink(String handle) {
    return new FileLink(config, cdnService, baseService, handle);
  }

  /**
   * Gets basic account info for this fsClient's API key.
   *
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   */
  public AppInfo getAppInfo() throws IOException {
    JsonObject params = CloudServiceUtil.buildBaseJson(config, null, null);
    Response<AppInfo> response = cloudService.prefetch(params);
    Util.checkResponseAndThrow(response);
    return response.getData();
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
  public CloudResponse getCloudItems(String providerName, @Nullable String path, @Nullable String next)
      throws IOException {

    Util.throwIfNullOrEmpty(providerName, "Provider name is required");
    Util.throwIfNullOrEmpty(path, "Path is required");

    JsonObject params = CloudServiceUtil.buildBaseJson(config, sessionToken, null);
    CloudServiceUtil.addCloudJson(params, providerName, path, next);
    Response<JsonObject> response = cloudService.list(params);
    Util.checkResponseAndThrow(response);
    JsonObject base = response.getData();

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
  public FileLink storeCloudItem(String providerName, String path, @Nullable StorageOptions options)
      throws IOException {

    Util.throwIfNullOrEmpty(providerName, "Provider name is required");
    Util.throwIfNullOrEmpty(path, "Path is required");

    if (options == null) {
      options = new StorageOptions.Builder().build();
    }

    JsonObject params = CloudServiceUtil.buildBaseJson(config, sessionToken, config.returnUrl);
    CloudServiceUtil.addCloudJson(params, providerName, path, null);
    CloudServiceUtil.addStorageJson(params, providerName, options);
    params.add("store", options.getAsJson());
    Response<JsonObject> response = cloudService.store(params);
    Util.checkResponseAndThrow(response);
    JsonElement responseJson = response.getData().get(providerName);
    Gson gson = new Gson();
    CloudStoreResponse storeInfo = gson.fromJson(responseJson, CloudStoreResponse.class);
    return new FileLink(config, cdnService, baseService, storeInfo.getHandle());
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

    JsonObject params = CloudServiceUtil.buildBaseJson(config, sessionToken, null);
    CloudServiceUtil.addCloudJson(params, providerName, null, null);
    Response<ResponseBody> response = cloudService.logout(params);
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
  public Flowable<Progress<FileLink>> uploadAsync(String path, boolean intel, @Nullable StorageOptions opts) {
    try {
      File inputFile = Util.createReadFile(path);
      InputStream inputStream = new FileInputStream(inputFile);

      if (opts == null) {
        opts = new StorageOptions.Builder().filename(inputFile.getName()).build();
      } else if (Util.isNullOrEmpty(opts.getFilename())) {
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
      InputStream input, int size, boolean intel, @Nullable StorageOptions opts) {
    if (opts == null) {
      opts = new StorageOptions.Builder().build();
    }

    Upload upload = new Upload(config, uploadService, input, size, intel, opts);
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
                                                     @Nullable final String next) {

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
                                              @Nullable final StorageOptions options) {

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
    return new ImageTransform(config, cdnService, url, true);
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
