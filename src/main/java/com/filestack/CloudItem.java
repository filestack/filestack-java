package org.filestack;

import java.io.Serializable;

public class CloudItem implements Serializable {
  private String name;
  private String path;
  private String mimetype;
  private String thumbnail;
  private String size;
  private long bytes;
  private boolean folder;

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof CloudItem)) {
      return false;
    }

    CloudItem item = (CloudItem) obj;

    return this.getPath().equals(item.getPath());
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public String getMimetype() {
    return mimetype;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  /** For some clouds a size string (such as "1000x1000") is returned instead of a filesize.*/
  public String getSizeString() {
    return size;
  }

  public long getSize() {
    return bytes;
  }

  public boolean isFolder() {
    return folder;
  }
}
