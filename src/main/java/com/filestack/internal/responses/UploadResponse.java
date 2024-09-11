package org.filestack.internal.responses;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/** Response object from multipart upload endpoint. */
@SuppressWarnings("unused")
public class UploadResponse {
  private String url;
  @SerializedName("location_url")
  private String locationUrl;
  @SerializedName("headers")
  private JsonObject s3Headers;

  public String getUrl() {
    return url;
  }

  public String getLocationUrl() {
    return locationUrl;
  }

  /**
   * Return {@link Map} of S3 headers.
   */
  public Map<String, String> getS3Headers() {
    HashMap<String, String> out = new HashMap<>();
    for (String key : s3Headers.keySet()) {
      out.put(key, s3Headers.get(key).getAsString());
    }
    return out;
  }
}
