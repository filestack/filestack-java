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
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.concurrent.Callable;
import retrofit2.Response;

/** Uploads new files. */
public class FilestackClient {
  // public static final String CLOUD_IMAGE_SEARCH = "imagesearch";
  public static final String CLOUD_FACEBOOK = "facebook";
  public static final String CLOUD_INSTAGRAM = "instagram";
  public static final String CLOUD_GOOGLE_DRIVE = "googledrive";
  public static final String CLOUD_DROPBOX = "dropbox";
  public static final String CLOUD_EVERNOTE = "evernote";
  public static final String CLOUD_FLICKR = "flickr";
  public static final String CLOUD_BOX = "box";
  public static final String CLOUD_GITHUB = "github";
  public static final String CLOUD_GMAIL = "gmail";
  public static final String CLOUD_GOOGLE_PHOTOS = "picasa";
  public static final String CLOUD_ONEDRIVE = "onedrive";
  public static final String CLOUD_AMAZON_DRIVE = "clouddrive";
  // public static final String CLOUD_CUSTOM_SOURCE = "customsource";
  // public static final String CLOUD_VIDEO = "video";

  private String apiKey;
  private Security security;
  private FsService fsService;
  private String returnUrl;
  private String sessionToken;

  /**
   * Constructs a client without security.
   *
   * @param apiKey account key from the dev portal
   */
  public FilestackClient(String apiKey) {
    this(apiKey, null);
  }

  /**
   * Constructs a client with security.
   *
   * @param apiKey   account key from the dev portal
   * @param security configured security object
   */
  public FilestackClient(String apiKey, Security security) {
    this(apiKey, security, null);
  }

  /**
   * Constructs a client using custom {@link FsService}. For internal use.
   *
   * @param apiKey    account key from the dev portal
   * @param security  configured security object
   * @param fsService service to use for API calls, overrides default singleton
   */
  public FilestackClient(String apiKey, Security security, FsService fsService) {
    this.apiKey = apiKey;
    this.security = security;
    this.fsService = fsService != null ? fsService : new FsService();
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
   * @throws HttpResponseException on error response from backend
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
   * Gets basic account info for this client's API key.
   *
   * @throws HttpResponseException on error response from backend
   * @throws IOException           on network failure
   */
  public AccountInfo getAccountInfo() throws IOException {
    JsonObject params = makeCloudParams();
    Response<AccountInfo> response = fsService.cloud().prefetch(params).execute();
    Util.checkResponseAndThrow(response);
    return response.body();
  }

  /**
   * Gets contents of a user's cloud "drive". If the user has not authorized for the provider, the
   * response will contain an OAuth URL that should be opened in a browser.
   *
   * @param providerName one of the static CLOUD constants in this class
   *
   * @throws HttpResponseException on error response from backend
   * @throws IOException           on network failure
   */
  public CloudContents getCloudContents(String providerName, String path) throws IOException {
    JsonObject params = makeCloudParams(providerName, path);
    Response<JsonObject> response = fsService.cloud().list(params).execute();
    Util.checkResponseAndThrow(response);
    JsonObject base = response.body();

    if (base.has("token")) {
      sessionToken = base.get("token").getAsString();
    }

    JsonElement provider = base.get(providerName);
    Gson gson = new Gson();
    return gson.fromJson(provider, CloudContents.class);
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
   * @throws HttpResponseException on error response from backend
   * @throws IOException           on network failure
   */
  public FileLink storeCloudItem(String providerName, String path, StorageOptions options)
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
    return new FileLink(apiKey, storeInfo.getHandle(), security);
  }

  /**
   * Logs out from specified cloud.
   *
   * @param providerName one of the static CLOUD constants in this class
   * @throws HttpResponseException on error response from backend
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

    Upload upload = new Upload(path, intelligent, options, this, fsService);
    return upload.runAsync();
  }

  /**
   * Asynchronously get basic account info for this client's API key.
   *
   * @see #getAccountInfo()
   */
  public Single<AccountInfo> getAccountInfoAsync() {
    return Single.fromCallable(new Callable<AccountInfo>() {
      @Override
      public AccountInfo call() throws Exception {
        return getAccountInfo();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  /**
   * Asynchronously gets contents of a user's cloud "drive".
   *
   * @see #getCloudContents(String, String)
   */
  public Single<CloudContents> getCloudContentsAsync(final String providerName, final String path) {
    return Single.fromCallable(new Callable<CloudContents>() {
      @Override
      public CloudContents call() throws Exception {
        return getCloudContents(providerName, path);
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
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
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
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
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
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
   * Creates a {@link JsonObject} with this client's config.
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
   * Creates a {@link JsonObject} with this client's config. Adds provider info.
   */
  protected JsonObject makeCloudParams(String providerName, String path) {
    JsonObject provider = new JsonObject();
    provider.addProperty("path", path);
    JsonObject clouds = new JsonObject();
    clouds.add(providerName, provider);
    JsonObject base = makeCloudParams();
    base.add("clouds", clouds);
    return base;
  }

  public String getApiKey() {
    return apiKey;
  }

  public Security getSecurity() {
    return security;
  }

  public FsService getFsService() {
    return fsService;
  }

  // TODO remove this and setup builder
  public void setReturnUrl(String returnUrl) {
    this.returnUrl = returnUrl;
  }

  public String getSessionToken() {
    return sessionToken;
  }

  public void setSessionToken(String sessionToken) {
    this.sessionToken = sessionToken;
  }
}
