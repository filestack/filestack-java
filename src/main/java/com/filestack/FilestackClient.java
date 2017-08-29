package com.filestack;

import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ValidationException;
import com.filestack.transforms.ImageTransform;
import com.filestack.util.FsService;
import com.filestack.util.Upload;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.concurrent.Callable;

/** Uploads new files. */
public class FilestackClient {
  private String apiKey;
  private Security security;

  private FsService fsService;
  private Integer delayBase = 2;

  /**
   * Constructs an instance without security.
   *
   * @see #FilestackClient(String, Security)
   */
  public FilestackClient(String apiKey) {
    this(apiKey, null);
  }

  /**
   * Constructs an instance with security.
   *
   * @param apiKey          account key from the dev portal
   * @param security        needs required permissions for your intended actions
   */
  public FilestackClient(String apiKey, Security security) {
    this.apiKey = apiKey;
    this.security = security;
    this.fsService = new FsService();
  }

  FilestackClient() {}

  /**
   * Builds new {@link FilestackClient}.
   */
  public static class Builder {
    private String apiKey;
    private Security security;
    private FsService fsService;
    private Integer delayBase;

    public Builder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public Builder security(Security security) {
      this.security = security;
      return this;
    }

    public Builder service(FsService fsService) {
      this.fsService = fsService;
      return this;
    }

    public Builder delayBase(int delayBase) {
      this.delayBase = delayBase;
      return this;
    }

    /**
     * Create the {@link FilestackClient} using the configured values.
     */
    public FilestackClient build() {
      FilestackClient client = new FilestackClient();
      client.apiKey = apiKey;
      client.security = security;
      client.fsService = fsService != null ? fsService : new FsService();
      client.delayBase = delayBase != null ? delayBase : 2;
      return client;
    }
  }

  /**
   * Upload local file using default storage options.
   *
   * @see #upload(String, StorageOptions, boolean)
   */
  public FileLink upload(String pathname)
      throws ValidationException, IOException, PolicySignatureException,
             InvalidParameterException, InternalException {
    return upload(pathname, null);
  }

  /**
   * Upload local file using custom storage options.
   *
   * @see #upload(String, StorageOptions, boolean)
   */
  public FileLink upload(String pathname, StorageOptions options)
      throws ValidationException, IOException, PolicySignatureException,
             InvalidParameterException, InternalException {
    return upload(pathname, options, true);
  }

  /**
   * Upload local file using custom storage and upload options.
   *
   * @param pathname    path to the file, can be local or absolute
   * @param options     storage options, https://www.filestack.com/docs/rest-api/store
   * @param intelligent intelligent ingestion, improves reliability for bad networks
   * @return new {@link FileLink} referencing file
   * @throws ValidationException       if the pathname doesn't exist or isn't a regular file
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if security is missing or invalid
   * @throws InvalidParameterException if a request parameter is missing or invalid
   * @throws InternalException         if unexpected error occurs
   */
  public FileLink upload(String pathname, StorageOptions options, boolean intelligent)
      throws ValidationException, IOException, PolicySignatureException,
             InvalidParameterException, InternalException {

    if (options == null) {
      options = new StorageOptions.Builder().build();
    }

    Upload upload = new Upload(pathname, options, intelligent, delayBase, this, fsService);
    return upload.run();
  }

  // Async method wrappers

  /**
   * Asynchronously upload local file using default storage options.
   *
   * @see #upload(String, StorageOptions, boolean)
   */
  public Single<FileLink> uploadAsync(String pathname) {
    return uploadAsync(pathname, null);
  }

  /**
   * Asynchronously upload local file using custom storage options.
   *
   * @see #upload(String, StorageOptions, boolean)
   */
  public Single<FileLink> uploadAsync(final String pathname, final StorageOptions options) {
    return uploadAsync(pathname, options, true);
  }

  /**
   * Asynchronously upload local file using custom storage and upload options.
   *
   * @see #upload(String, StorageOptions, boolean)
   */
  public Single<FileLink> uploadAsync(final String pathname, final StorageOptions options,
                                      final boolean intelligent) {

    return Single.fromCallable(new Callable<FileLink>() {
      @Override
      public FileLink call() throws Exception {
        return upload(pathname, options, intelligent);
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  /**
   * Creates an {@link ImageTransform} object for this file.
   * A transformation call isn't made directly by this method.
   *
   * @return {@link ImageTransform ImageTransform} instance configured for this file
   */
  public ImageTransform imageTransform(String url) {
    return new ImageTransform(this, url);
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
