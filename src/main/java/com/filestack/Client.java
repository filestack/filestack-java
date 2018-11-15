package com.filestack;

import com.filestack.internal.*;
import com.filestack.internal.responses.CloudStoreResponse;
import com.filestack.transforms.ImageTransform;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.ResponseBody;

import javax.annotation.Nullable;
import java.io.*;

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

  public FileLink upload(String path, boolean intel) throws Exception {
    return upload(path, intel, null);
  }

  public FileLink upload(String path, boolean intel, @Nullable StorageOptions opts) throws Exception {
    File inputFile = Util.createReadFile(path);
    InputStream inputStream = new FileInputStream(inputFile);

    if (opts == null) {
      opts = new StorageOptions.Builder().filename(inputFile.getName()).build();
    } else if (Util.isNullOrEmpty(opts.getFilename())) {
      opts = opts.newBuilder().filename(inputFile.getName()).build();
    }

    return upload(inputStream, (int) inputFile.length(), intel, opts);
  }

  public FileLink upload(InputStream input, int size, boolean intel) throws Exception {
    return upload(input, size, intel, null);
  }

  /**
   * Synchronously uploads an {@link InputStream}.
   *
   */
  public FileLink upload(InputStream input, int size, boolean intel, @Nullable StorageOptions opts)
      throws Exception {
    if (opts == null) {
      opts = new StorageOptions.Builder().build();
    }
    final Upload upload = new Upload(config, uploadService, input, size, intel, opts);
    return upload.upload();
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
    JsonObject params = CloudServiceUtil.buildBaseJson(config, null);
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

    JsonObject params = CloudServiceUtil.buildBaseJson(config, sessionToken);
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

    JsonObject params = CloudServiceUtil.buildBaseJson(config, sessionToken);
    CloudServiceUtil.addCloudJson(params, providerName, null, null);
    Response<ResponseBody> response = cloudService.logout(params);
    Util.checkResponseAndThrow(response);
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
