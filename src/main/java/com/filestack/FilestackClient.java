package com.filestack;

import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ValidationException;
import com.filestack.transforms.ImageTransform;
import com.filestack.util.Upload;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Uploads new files.
 */
public class FilestackClient {
  private String apiKey;
  private Security security;

  /**
   * Constructs an instance without security.
   *
   * @param apiKey account key from the dev portal
   */
  public FilestackClient(String apiKey) {
    this.apiKey = apiKey;
  }

  /**
   * Constructs an instance with security.
   *
   * @param apiKey   account key from the dev portal
   * @param security needs required permissions for your intended actions
   */
  public FilestackClient(String apiKey, Security security) {
    this.apiKey = apiKey;
    this.security = security;
  }

  /**
   * Upload local file to Filestack using default storage options.
   *
   * @param pathname path to the file, can be local or absolute
   * @return new {@link FileLink} referencing file
   * @throws ValidationException       if the pathname doesn't exist or isn't a regular file
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if policy and/or signature are invalid or inadequate
   * @throws InvalidParameterException if a request parameter is missing or invalid
   * @throws InternalException         if unexpected error occurs
   */
  public FileLink upload(String pathname)
      throws ValidationException, IOException, PolicySignatureException,
             InvalidParameterException, InternalException {
    UploadOptions defaultOptions = new UploadOptions.Builder().build();
    return upload(pathname, defaultOptions);
  }

  /**
   * Upload local file to Filestack using custom storage options.
   *
   * @param pathname path to the file, can be local or absolute
   * @param options  storage options, https://www.filestack.com/docs/rest-api/store
   * @return new {@link FileLink} referencing file
   * @throws ValidationException       if the pathname doesn't exist or isn't a regular file
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if policy and/or signature are invalid or inadequate
   * @throws InvalidParameterException if a request parameter is missing or invalid
   * @throws InternalException         if unexpected error occurs
   */
  public FileLink upload(String pathname, UploadOptions options)
      throws ValidationException, IOException, PolicySignatureException,
             InvalidParameterException, InternalException {
    Upload upload = new Upload(pathname, this, options);
    return upload.run();
  }

  // Async method wrappers

  /**
   * Async, observable version of {@link #upload(String)}.
   * Same exceptions are passed through observable.
   */
  public Single<FileLink> uploadAsync(String filepath) {
    UploadOptions defaultOptions = new UploadOptions.Builder().build();
    return uploadAsync(filepath, defaultOptions);
  }

  /**
   * Async, observable version of {@link #upload(String, UploadOptions)}.
   * Same exceptions are passed through observable.
   */
  public Single<FileLink> uploadAsync(final String filepath, final UploadOptions options) {
    return Single.fromCallable(new Callable<FileLink>() {
      @Override
      public FileLink call() throws Exception {
        return upload(filepath, options);
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
