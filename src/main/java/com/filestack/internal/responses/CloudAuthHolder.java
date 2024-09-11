package org.filestack.internal.responses;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class CloudAuthHolder {
  @SerializedName("redirect_url") private String redirectUrl;

  public String getRedirectUrl() {
    return redirectUrl;
  }
}
