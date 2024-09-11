package org.filestack.internal.responses;

/** Response object from transform store endpoint. */
@SuppressWarnings("unused")
public class StoreResponse {
  private String url;
  private String filename;
  private String type;

  private String container;
  private String key;

  private int width;
  private int height;
  private int size;

  public String getUrl() {
    return url;
  }

  public String getFilename() {
    return filename;
  }

  public String getType() {
    return type;
  }

  public String getContainer() {
    return container;
  }

  public String getKey() {
    return key;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getSize() {
    return size;
  }
}
