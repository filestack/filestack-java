package org.filestack;

import com.google.gson.annotations.SerializedName;

public class AppInfo {
  @SerializedName("intelligent_ingestion") private boolean intelligent;
  private boolean blocked;
  @SerializedName("whitelabel") private boolean whiteLabel;
  @SerializedName("customsource") private boolean customSource;

  public boolean isIntelligent() {
    return intelligent;
  }

  public boolean isBlocked() {
    return blocked;
  }

  public boolean isWhiteLabel() {
    return whiteLabel;
  }

  public boolean isCustomSource() {
    return customSource;
  }
}
