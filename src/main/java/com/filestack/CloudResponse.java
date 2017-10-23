package com.filestack;

import com.filestack.util.responses.CloudAuthHolder;

public class CloudResponse {
  private CloudAuthHolder auth;
  private CloudItem[] items;
  private String client;
  private String filename;
  private String next;

  /** Returns an OAuth URL or null if the user has already authorized. */
  public String getAuthUrl() {
    return auth != null ? auth.getRedirectUrl() : null;
  }

  public CloudItem[] getItems() {
    return items;
  }

  public String getProvider() {
    return client;
  }

  public String getDirectory() {
    return filename;
  }

  /** Returns a pagination token if all items can't be returned at once. */
  public String getNextToken() {
    // An empty string token is confusing, just return null
    if (next == null || next.equals("")) {
      return null;
    }
    return next;
  }
}
