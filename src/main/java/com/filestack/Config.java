package org.filestack;

import org.filestack.internal.Util;
import org.filestack.transforms.Transform;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Holds config common to {@link Client}, {@link FileLink}, and {@link Transform} classes.
 */
public class Config implements Serializable {
  protected final String apiKey;
  protected final String policy;
  protected final String signature;
  protected final String returnUrl;

  /**
   * Constructs configuration for {@link Client} class.
   * @param apiKey - an API key obtained from the Developer Portal
   * @param policy - access policy, one can be created with {@link Policy.Builder}
   */
  public Config(String apiKey, Policy policy) {
    this(apiKey, null, policy.getEncodedPolicy(), policy.getSignature());
  }

  /**
   * Constructs basic configuration for {@link Client} class without any security policy.
   * @param apiKey - an API key obtained from the Developer Portal
  */
  public Config(String apiKey) {
    this(apiKey, null, null, null);
  }

  /**
   * Constructs basic configuration for {@link Client} class without any security policy.
   * @param apiKey - an API key obtained from the Developer Portal
   * @param returnUrl - returnUrl used for building JSON bodies with {@link org.filestack.internal.CloudServiceUtil}
   * @deprecated use {@link #Config(String)} instead and manually pass returnUrl to
   *     {@link org.filestack.internal.CloudServiceUtil#buildBaseJson(Config, String, String)} if necessary
   */
  @Deprecated
  public Config(String apiKey, @Nullable String returnUrl) {
    this(apiKey, returnUrl, null, null);
  }

  /**
   * Constructs configuration for {@link Client} class.
   * @param apiKey - an API key obtained from the Developer Portal
   * @param encodedPolicy - encoded policy, obtain one using {@link Policy#getEncodedPolicy()}
   * @param signature - policy signature, obtain one using {@link Policy#getSignature()}
   */
  public Config(String apiKey, @Nullable String encodedPolicy, @Nullable String signature) {
    this(apiKey, null, encodedPolicy, signature);
  }

  /**
   * Constructs configuration for {@link Client} class.
   * @param apiKey - an API key obtained from the Developer Portal
   * @param encodedPolicy - encoded policy, obtain one using {@link Policy#getEncodedPolicy()}
   * @param signature - policy signature, obtain one using {@link Policy#getSignature()}
   * @param returnUrl - returnUrl used for building JSON bodies with {@link org.filestack.internal.CloudServiceUtil}
   * @deprecated use {@link #Config(String, String, String)} instead and manually pass returnUrl to
   *     {@link org.filestack.internal.CloudServiceUtil#buildBaseJson(Config, String, String)} if necessary
   */
  @Deprecated
  public Config(String apiKey, @Nullable String returnUrl, @Nullable String encodedPolicy,
                @Nullable String signature) {
    this.apiKey = apiKey;
    this.returnUrl = returnUrl;
    this.policy = encodedPolicy;
    this.signature = signature;
  }

  public String getApiKey() {
    return apiKey;
  }

  @Deprecated
  public String getReturnUrl() {
    return returnUrl;
  }

  public boolean hasSecurity() {
    return !(Util.isNullOrEmpty(policy) || Util.isNullOrEmpty(signature));
  }

  public String getPolicy() {
    return policy;
  }

  public String getSignature() {
    return signature;
  }
}
