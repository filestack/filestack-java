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
   * @param apiKey   account key from the dev portal
   * @param security needs required permissions for your intended actions
   */
  public FilestackClient(String apiKey, Security security) {
    this(apiKey, security, null);
  }

  /**
   * Constructs an instance with security and custom {@link FsService}.
   *
   * @param apiKey   account key from the dev portal
   * @param security needs required permissions for your intended actions
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
  public FileLink upload(String path, boolean intelligent)
      throws ValidationException, IOException, PolicySignatureException,
      InvalidParameterException, InternalException {
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
   * @throws ValidationException       if the pathname doesn't exist or isn't a regular file
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if security is missing or invalid
   * @throws InvalidParameterException if a request parameter is missing or invalid
   * @throws InternalException         if unexpected error occurs
   */
  public FileLink upload(String path, boolean intelligent, StorageOptions options)
      throws ValidationException, IOException, PolicySignatureException,
             InvalidParameterException, InternalException {

    try {
      return uploadAsync(path, intelligent, options).blockingLast().getData();
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

  /**
   * Asynchronously uploads local file using default storage options.
   *
   * @see #upload(String, boolean, StorageOptions)
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
      options = options.newBuilder()
          .contentType("application/octet-stream")
          .build();
    }

    Upload upload = new Upload(path, intelligent, options, this, fsService);
    return upload.runAsync();
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
