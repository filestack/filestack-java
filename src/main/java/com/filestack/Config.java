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

  /** Construct basic config. */
  public Config(String apiKey) {
    this.apiKey = apiKey;
    this.policy = null;
    this.signature = null;
  }

  /** Construct config with security. */
  public Config(String apiKey, String policy, String signature) {
    this.apiKey = apiKey;
    this.policy = policy;
    this.signature = signature;
  }

  public String getApiKey() {
    return apiKey;
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
