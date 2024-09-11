package org.filestack;

import org.filestack.internal.responses.CloudAuthHolder;
import com.google.gson.annotations.SerializedName;

public class CloudResponse {
  private CloudAuthHolder auth;
  @SerializedName("contents")
  private CloudItem[] items;
  @SerializedName("client")
  private String provider;
  @SerializedName("filename")
  private String directory;
  @SerializedName("next")
  private String nextToken;

  /** Returns an OAuth URL or null if the user has already authorized. */
  public String getAuthUrl() {
    return auth != null ? auth.getRedirectUrl() : null;
  }

  public CloudItem[] getItems() {
    return items;
  }

  public String getProvider() {
    return provider;
  }

  public String getDirectory() {
    return directory;
  }

  /** Returns a pagination token if all items can't be returned at once. */
  public String getNextToken() {
    // An empty string token is confusing, just return null
    if (nextToken == null || nextToken.equals("")) {
      return null;
    }
    return nextToken;
  }
}
