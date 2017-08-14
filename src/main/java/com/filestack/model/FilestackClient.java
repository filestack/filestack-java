package com.filestack.model;

import com.filestack.model.transform.base.ImageTransform;
import com.filestack.util.Upload;
import com.filestack.util.UploadOptions;

import java.io.IOException;

/**
 * Wrapper for communicating with the Filestack REST API.
 * Instantiate with an API Key from the Developer Portal.
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
   * @param filepath path to the file, can be local or absolute
   * @return new {@link FileLink} referencing file
   * @throws IOException for network failures or invalid security
   */
  public FileLink upload(String filepath) throws IOException {
    UploadOptions defaultOptions = new UploadOptions.Builder().build();
    return upload(filepath, defaultOptions);
  }

  /**
   * Upload local file to Filestack using custom storage options.
   *
   * @param filepath path to the file, can be local or absolute
   * @param options storage options, https://www.filestack.com/docs/rest-api/store
   * @return new {@link FileLink} referencing file
   * @throws IOException or network failures or invalid security
   */
  public FileLink upload(String filepath, UploadOptions options) throws IOException {
    Upload upload = new Upload(filepath, this, options);
    return upload.run();
  }

  /**
   * Creates an image transformation object for this file.
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
