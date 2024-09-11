package org.filestack.internal.responses;

@SuppressWarnings("unused")
public class CloudStoreResponse {
  private String url;
  private String handle;
  private String filename;
  private String mimetype;
  private long size;
  private String client;

  public String getUrl() {
    return url;
  }

  public String getHandle() {
    return handle;
  }

  public String getFilename() {
    return filename;
  }

  public String getMimetype() {
    return mimetype;
  }

  public long getSize() {
    return size;
  }

  public String getProviderId() {
    return client;
  }
}
