package com.filestack;

import com.filestack.util.responses.CloudAuthHolder;

public class CloudContents {
  private CloudAuthHolder auth;
  private CloudItem[] contents;
  private String client;
  private String filename;
  private String next;

  /** Returns an OAuth URL or null if the user has already authorized. */
  public String getAuthUrl() {
    return auth != null ? auth.getRedirectUrl() : null;
  }

  public CloudItem[] getItems() {
    return contents;
  }

  public String getProvider() {
    return client;
  }

  public String getDirectory() {
    return filename;
  }

  public String getNextToken() {
    return next;
  }
}
