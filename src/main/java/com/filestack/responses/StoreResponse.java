package com.filestack.responses;

/** Response object from transform store endpoint. */
public class StoreResponse {
  String url;
  String filename;
  String type;

  String container;
  String key;

  int width;
  int height;
  int size;

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
