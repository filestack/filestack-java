package com.filestack;

import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ValidationException;
import com.filestack.transforms.ImageTransform;
import com.filestack.util.FsUploadService;
import com.filestack.util.Networking;
import com.filestack.util.Upload;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.concurrent.Callable;

/** Uploads new files. */
public class FilestackClient {
  private String apiKey;
  private Security security;

  private FsUploadService fsUploadService;

  /**
   * Constructs an instance without security.
   *
   * @see #FilestackClient(String, Security, FsUploadService)
   */
  public FilestackClient(String apiKey) {
    this(apiKey, null, null);
  }

  /**
   * Constructs an instance without security using provided {@link FsUploadService}.
   *
   * @see #FilestackClient(String, Security, FsUploadService)
   */
  public FilestackClient(String apiKey, FsUploadService fsUploadService) {
    this(apiKey, null, fsUploadService);
  }

  /**
   * Constructs an instance with security.
   *
   * @see #FilestackClient(String, Security, FsUploadService)
   */
  public FilestackClient(String apiKey, Security security) {
    this(apiKey, security, null);
  }

  /**
   * Constructs an instance with security using provided {@link FsUploadService}.
   *
   * @param apiKey          account key from the dev portal
   * @param security        needs required permissions for your intended actions
   * @param fsUploadService service to use instead of global singleton
   */
  public FilestackClient(String apiKey, Security security, FsUploadService fsUploadService) {
    this.apiKey = apiKey;
    this.security = security;
    if (fsUploadService != null) {
      this.fsUploadService = fsUploadService;
    } else {
      this.fsUploadService = Networking.getFsUploadService();
    }
  }

  /**
   * Upload local file using default storage options.
   *
   * @see #upload(String, UploadOptions, Integer)
   */
  public FileLink upload(String pathname)
      throws ValidationException, IOException, PolicySignatureException,
             InvalidParameterException, InternalException {
    return upload(pathname, null, null);
  }

  /**
   * Upload local file using default storage options and custom retry delay.
   *
   * @see #upload(String, UploadOptions, Integer)
   */
  public FileLink upload(String pathname, int delayBase)
      throws ValidationException, IOException, PolicySignatureException,
      InvalidParameterException, InternalException {
    return upload(pathname, null, delayBase);
  }

  /**
   * Upload local file using custom storage options.
   *
   * @see #upload(String, UploadOptions, Integer)
   */
  public FileLink upload(String pathname, UploadOptions options)
      throws ValidationException, IOException, PolicySignatureException,
             InvalidParameterException, InternalException {
    return upload(pathname, options, null);
  }

  /**
   * Upload local file using custom storage options and retry delay.
   *
   * @param pathname  path to the file, can be local or absolute
   * @param options   storage options, https://www.filestack.com/docs/rest-api/store
   * @param delayBase base for exponential backoff, delay (seconds) == base ^ retryCount
   * @return new {@link FileLink} referencing file
   * @throws ValidationException       if the pathname doesn't exist or isn't a regular file
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if policy and/or signature are invalid or inadequate
   * @throws InvalidParameterException if a request parameter is missing or invalid
   * @throws InternalException         if unexpected error occurs
   */
  public FileLink upload(String pathname, UploadOptions options, Integer delayBase)
      throws ValidationException, IOException, PolicySignatureException,
      InvalidParameterException, InternalException {

    if (options == null) {
      options = new UploadOptions.Builder().build();
    }

    if (delayBase == null) {
      delayBase = 2;
    }

    Upload upload = new Upload(pathname, this, options, fsUploadService, delayBase);
    return upload.run();
  }

  // Async method wrappers

  /**
   * Asynchronously upload local file using default storage options.
   *
   * @see #upload(String, UploadOptions, Integer)
   */
  public Single<FileLink> uploadAsync(String pathname) {
    return uploadAsync(pathname, null, null);
  }

  /**
   * Asynchronously upload local file using default storage options and custom retry delay.
   *
   * @see #upload(String, UploadOptions, Integer)
   */
  public Single<FileLink> uploadAsync(String pathname, Integer delayBase) {
    return uploadAsync(pathname, null, delayBase);
  }

  /**
   * Asynchronously upload local file using custom storage options.
   *
   * @see #upload(String, UploadOptions, Integer)
   */
  public Single<FileLink> uploadAsync(String pathname, UploadOptions options) {
    return uploadAsync(pathname, options, null);
  }

  /**
   * Asynchronously upload local file using custom storage options and retry delay.
   *
   * @see #upload(String, UploadOptions, Integer)
   */
  public Single<FileLink> uploadAsync(final String pathname, final UploadOptions options,
                                      final Integer delayBase) {

    return Single.fromCallable(new Callable<FileLink>() {
      @Override
      public FileLink call() throws Exception {
        return upload(pathname, options, delayBase);
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
}
