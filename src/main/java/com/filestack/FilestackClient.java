package com.filestack;

import com.filestack.transforms.ImageTransform;
import com.filestack.util.FsService;
import com.filestack.util.Upload;
import com.filestack.util.Util;
import com.google.gson.JsonObject;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.concurrent.Callable;
import retrofit2.Response;

/** Uploads new files. */
public class FilestackClient {
  private String apiKey;
  private Security security;
  private FsService fsService;

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
   * Get basic account info for this client's API key.
   *
   * @throws HttpResponseException on error response from backend
   * @throws IOException           on network failure
   */
  public AccountInfo getAccountInfo() throws IOException {
    JsonObject params = getConfigJson();
    Response<AccountInfo> response = fsService.cloud().prefetch(params).execute();
    Util.checkResponseAndThrow(response);
    return response.body();
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
   * Creates a {@link JsonObject} with this client's API key, policy, and signature.
   */
  protected JsonObject getConfigJson() {
    JsonObject json = new JsonObject();
    json.addProperty("apikey", apiKey);
    if (security != null) {
      json.addProperty("policy", security.getPolicy());
      json.addProperty("signature", security.getSignature());
    }
    return json;
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
}
