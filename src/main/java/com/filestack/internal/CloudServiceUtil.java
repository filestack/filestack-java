package org.filestack.internal;

import org.filestack.Config;
import org.filestack.StorageOptions;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;

/**
 * Functions to help create JSON bodies for Cloud API.
 */
public class CloudServiceUtil {

  private static final String KEY_API_KEY = "apikey";
  private static final String KEY_FLOW = "flow";
  private static final String KEY_POLICY = "policy";
  private static final String KEY_SIGNATURE = "signature";
  private static final String KEY_APP_URL = "appurl";
  private static final String KEY_TOKEN = "token";
  private static final String KEY_CLOUDS = "clouds";
  private static final String KEY_PATH = "path";
  private static final String KEY_NEXT = "next";
  private static final String KEY_STORE = "store";

  private static final String VALUE_FLOW_MOBILE = "mobile";


  /**
   * Create the base JSON object with properties needed for all requests.
   * @deprecated explicitly pass returnUrl with {@link #buildBaseJson(Config, String, String)} instead.
   */
  @Deprecated
  public static JsonObject buildBaseJson(Config config, String session) {
    return buildBaseJson(config, session, config.getReturnUrl());
  }

  /**
   * Create the base JSON object with properties needed for all requests.
   */
  public static JsonObject buildBaseJson(Config config, @Nullable String session, @Nullable String returnUrl) {

    JsonObject json = new JsonObject();
    json.addProperty(KEY_API_KEY, config.getApiKey());
    json.addProperty(KEY_FLOW, VALUE_FLOW_MOBILE);

    if (config.hasSecurity()) {
      json.addProperty(KEY_POLICY, config.getPolicy());
      json.addProperty(KEY_SIGNATURE, config.getSignature());
    }

    if (returnUrl != null) {
      json.addProperty(KEY_APP_URL, returnUrl);
    }

    if (session != null) {
      json.addProperty(KEY_TOKEN, session);
    }

    json.add(KEY_CLOUDS, new JsonObject());

    return json;
  }

  /**
   * Add JSON object for a cloud. For list, store, and logout requests.
   */
  public static void addCloudJson(JsonObject base, String cloud, @Nullable String path, @Nullable String next) {
    JsonObject json = new JsonObject();

    if (path != null) {
      json.addProperty(KEY_PATH, path);
    }

    if (next != null) {
      json.addProperty(KEY_NEXT, next);
    }

    base.getAsJsonObject(KEY_CLOUDS).add(cloud, json);
  }

  /**
   * Add JSON object for storage options.
   */
  public static void addStorageJson(JsonObject base, String cloud, StorageOptions options) {
    JsonObject json = options.getAsJson();

    base.getAsJsonObject(KEY_CLOUDS).getAsJsonObject(cloud).add(KEY_STORE, json);
  }

  private CloudServiceUtil() {

  }

}
