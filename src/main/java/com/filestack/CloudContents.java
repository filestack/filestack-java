package com.filestack;

import com.filestack.util.responses.CloudAuthHolder;

public class CloudContents {
  private CloudAuthHolder auth;
  private CloudItem[] contents;
  private String client;
  private String filename;
  private String name;
  private String next;

  /** Returns an OAuth URL or null if the user has already authorized. */
  public String getAuthUrl() {
    return auth != null ? auth.getRedirectUrl() : null;
  }

  public CloudItem[] getItems() {
    return contents;
  }

  public String getProviderId() {
    return client;
  }

  // There's possibly an issue with the filename and name values
  // "filename" contains a proper title for the provider, "name" is empty

  public String getProviderName() {
    return filename;
  }

  /*
  public String getFilename() {
    return filename;
  }
  */

  /*
  public String getName() {
    return name;
  }
  */

  public String getNextToken() {
    return next;
  }
}
