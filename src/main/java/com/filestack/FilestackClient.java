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
  String apiKey;
  Security security;

  FsService fsService;
  Integer delayBase = 2;

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
    private FilestackClient building = new FilestackClient();

    public Builder apiKey(String apiKey) {
      building.apiKey = apiKey;
      return this;
    }

    public Builder security(Security security) {
      building.security = security;
      return this;
    }

    public Builder service(FsService fsService) {
      building.fsService = fsService;
      return this;
    }

    public Builder delayBase(int delayBase) {
      building.delayBase = delayBase;
      return this;
    }

    public FilestackClient build() {
      return building;
    }
  }

  /**
   * Upload local file using default storage options.
   *
   * @see #upload(String, UploadOptions)
   */
  public FileLink upload(String pathname)
      throws ValidationException, IOException, PolicySignatureException,
             InvalidParameterException, InternalException {
    return upload(pathname, null);
  }

  /**
   * Upload local file using custom storage options.
   *
   * @param pathname  path to the file, can be local or absolute
   * @param options   storage options, https://www.filestack.com/docs/rest-api/store
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
    if (options == null) {
      options = new UploadOptions.Builder().build();
    }

    Upload upload = new Upload(pathname, this, options, fsService, delayBase);
    return upload.run();
  }

  // Async method wrappers

  /**
   * Asynchronously upload local file using default storage options.
   *
   * @see #upload(String, UploadOptions)
   */
  public Single<FileLink> uploadAsync(String pathname) {
    return uploadAsync(pathname, null);
  }

  /**
   * Asynchronously upload local file using custom storage options.
   *
   * @see #upload(String, UploadOptions)
   */
  public Single<FileLink> uploadAsync(final String pathname, final UploadOptions options) {
    return Single.fromCallable(new Callable<FileLink>() {
      @Override
      public FileLink call() throws Exception {
        return upload(pathname, options);
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
