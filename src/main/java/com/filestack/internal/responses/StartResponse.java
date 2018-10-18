package com.filestack.internal.responses;

import com.filestack.internal.Util;
import com.google.gson.annotations.SerializedName;
import okhttp3.RequestBody;

import java.util.HashMap;
import java.util.Map;

/** Response object from multipart start endpoint. */
@SuppressWarnings("unused")
public class StartResponse {
  private String uri;
  private String region;
  @SerializedName("location_url")
  private String locationUrl;
  @SerializedName("upload_id")
  private String uploadId;
  @SerializedName("upload_type")
  private String uploadType;

  StartResponse(String uri, String region, String locationUrl, String uploadId, String uploadType) {
    this.uri = uri;
    this.region = region;
    this.locationUrl = locationUrl;
    this.uploadId = uploadId;
    this.uploadType = uploadType;
  }

  StartResponse() {
    //no-arg constructor for gson
  }

  /**
   * Return {@link Map} of params needed for subsequent multipart calls. For convenience.
   */
  public Map<String, RequestBody> getUploadParams() {
    Map<String, RequestBody> parameters = new HashMap<>();
    parameters.put("uri", Util.createStringPart(uri));
    parameters.put("region", Util.createStringPart(region));
    parameters.put("upload_id", Util.createStringPart(uploadId));
    return parameters;
  }

  public boolean isIntelligent() {
    return uploadType != null && uploadType.equals("intelligent_ingestion");
  }
}
