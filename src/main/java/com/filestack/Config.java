package com.filestack;

import com.filestack.transforms.Transform;

import java.io.Serializable;

/**
 * Holds config common to {@link Client}, {@link FileLink}, and {@link Transform} classes.
 */
public class Config implements Serializable {
  protected final String apiKey;
  protected final String policy;
  protected final String signature;
  protected final String returnUrl;

  /** Construct basic config. */
  public Config(String apiKey) {
    this.apiKey = apiKey;
    this.returnUrl = null;
    this.policy = null;
    this.signature = null;
  }

  /** Construct config for auth.*/
  public Config(String apiKey, String returnUrl) {
    this.apiKey = apiKey;
    this.returnUrl = returnUrl;
    this.policy = null;
    this.signature = null;
  }

  /** Construct config for security. */
  public Config(String apiKey, String policy, String signature) {
    this.apiKey = apiKey;
    this.returnUrl = null;
    this.policy = policy;
    this.signature = signature;
  }

  /** Construct config for auth and security. */
  public Config(String apiKey, String returnUrl, String policy, String signature) {
    this.apiKey = apiKey;
    this.returnUrl = returnUrl;
    this.policy = policy;
    this.signature = signature;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getReturnUrl() {
    return returnUrl;
  }

  public boolean hasSecurity() {
    return policy != null && signature != null;
  }

  public String getPolicy() {
    return policy;
  }

  public String getSignature() {
    return signature;
  }
}
