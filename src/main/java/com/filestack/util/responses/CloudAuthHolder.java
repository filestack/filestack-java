package com.filestack.util.responses;

import com.google.gson.annotations.SerializedName;

public class CloudAuthHolder {
  @SerializedName("redirect_url") private String redirectUrl;

  public String getRedirectUrl() {
    return redirectUrl;
  }
}
