package com.filestack;

import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ResourceNotFoundException;
import com.filestack.errors.ValidationException;
import com.filestack.transforms.ImageTransform;
import com.filestack.util.FsService;
import com.filestack.util.Upload;
import com.filestack.util.Util;
import io.reactivex.Flowable;
import java.io.IOException;

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
   * Uploads local file using default storage options.
   *
   * @see #upload(String, String, StorageOptions, boolean)
   */
  public FileLink upload(String path, String contentType)
      throws ValidationException, IOException, PolicySignatureException,
             InvalidParameterException, InternalException {
    return upload(path, contentType, null);
  }

  /**
   * Upload local file using custom storage options.
   *
   * @see #upload(String, String, StorageOptions, boolean)
   */
  public FileLink upload(String path, String contentType, StorageOptions options)
      throws ValidationException, IOException, PolicySignatureException,
             InvalidParameterException, InternalException {
    return upload(path, contentType, options, true);
  }

  /**
   * Uploads local file using custom storage and upload options.
   *
   * @param path           path to the file, can be local or absolute
   * @param contentType    MIME type of the file
   * @param options storage options, https://www.filestack.com/docs/rest-api/store
   * @param intelligent    intelligent ingestion, improves reliability for bad networks
   * @return new {@link FileLink} referencing file
   * @throws ValidationException       if the pathname doesn't exist or isn't a regular file
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if security is missing or invalid
   * @throws InvalidParameterException if a request parameter is missing or invalid
   * @throws InternalException         if unexpected error occurs
   */
  public FileLink upload(String path, String contentType, StorageOptions options,
                         boolean intelligent)
      throws ValidationException, IOException, PolicySignatureException,
             InvalidParameterException, InternalException {

    try {
      return uploadAsync(path, contentType, options, intelligent).blockingLast().getData();
    } catch (RuntimeException e) {
      try {
        Util.castExceptionAndThrow(e.getCause());
      } catch (ResourceNotFoundException ee) {
        // Shouldn't get a 404 so if we do, indicate an unexpected error
        throw new InternalException(ee);
      }
    }

    return null;
  }

  // Async methods
  // Unlike the FileLink methods, the sync upload call actually wraps the async call

  /**
   * Asynchronously uploads local file using default storage options.
   *
   * @see #upload(String, String, StorageOptions, boolean)
   * @see #uploadAsync(String, String, StorageOptions, boolean)
   */
  public Flowable<Progress<FileLink>> uploadAsync(String path, String contentType) {
    return uploadAsync(path, contentType, null);
  }

  /**
   * Asynchronously uploads local file using custom storage options.
   *
   * @see #upload(String, String, StorageOptions, boolean)
   * @see #uploadAsync(String, String, StorageOptions, boolean)
   */
  public Flowable<Progress<FileLink>> uploadAsync(String path, String contentType,
                                                  StorageOptions options) {
    return uploadAsync(path, contentType, options, true);
  }

  /**
   * Asynchronously uploads local file using custom storage and upload options.
   * A stream of {@link Progress} objects are emitted by the returned {@link Flowable}.
   * The final {@link Progress} object will return a new {@link FileLink} from
   * {@link Progress#getData()}. The upload is not done until {@link Progress#getData()} returns
   * non-null.
   *
   * @see #upload(String, String, StorageOptions, boolean)
   */
  public Flowable<Progress<FileLink>> uploadAsync(String path, String contentType,
                                                  StorageOptions options, boolean intelligent) {

    if (options == null) {
      options = new StorageOptions.Builder().build();
    }

    Upload upload = new Upload(path, contentType, options, intelligent, delayBase, this, fsService);
    return upload.runAsync();
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
