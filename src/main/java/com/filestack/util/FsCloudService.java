package com.filestack.util;

import com.google.gson.JsonObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FsCloudService {
  String URL = "https://cloud.filestackapi.com/";

  @POST("prefetch")
  Call<JsonObject> prefetch(@Body JsonObject body);

  @POST("folder/list")
  Call<JsonObject> list(@Body JsonObject body);

  // Cloudrouter requires the trailing slash
  @POST("store/")
  Call<JsonObject> store(@Body JsonObject body);
}
