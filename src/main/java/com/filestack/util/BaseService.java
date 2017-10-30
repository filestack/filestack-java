package com.filestack.util;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/** Wraps endpoints that run on www.filestackapi.com. */
public interface BaseService {
  String URL = "https://www.filestackapi.com/api/file/";

  @POST("{handle}")
  Call<ResponseBody> overwrite(
      @Path("handle") String handle,
      @Query("policy") String policy,
      @Query("signature") String signature,
      @Body RequestBody body);

  @DELETE("{handle}")
  Call<ResponseBody> delete(
      @Path("handle") String handle,
      @Query("key") String key,
      @Query("policy") String policy,
      @Query("signature") String signature);
}
