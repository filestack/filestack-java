package org.filestack.internal.responses;

import org.filestack.internal.Util;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;
import okhttp3.RequestBody;

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

  /**
   * Return {@link Map} of params needed for subsequent multipart calls. For convenience.
   */
  public Map<String, RequestBody> getUploadParams() {
    HashMap<String, RequestBody> parameters = new HashMap<>();
    parameters.put("uri", Util.createStringPart(uri));
    parameters.put("region", Util.createStringPart(region));
    parameters.put("upload_id", Util.createStringPart(uploadId));
    return parameters;
  }

  public boolean isIntelligent() {
    return uploadType != null && uploadType.equals("intelligent_ingestion");
  }
}
