package com.filestack.util;

import com.filestack.util.responses.StoreResponse;
import com.google.gson.JsonObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/** Wraps endpoints that run on cdn.filestackcontent.com. */
public interface FsCdnService {
  String URL = "https://cdn.filestackcontent.com/";

  @GET("{handle}")
  @Streaming
  Call<ResponseBody> get(
      @Path("handle") String handle,
      @Query("policy") String policy,
      @Query("signature") String signature);

  // Using an existing handle
  @Streaming
  @GET("{tasks}/{handle}")
  Call<ResponseBody> transform(
      @Path("tasks") String tasks,
      @Path("handle") String handle);

  @GET("debug/{tasks}/{handle}")
  Call<JsonObject> transformDebug(
      @Path("tasks") String tasks,
      @Path("handle") String handle);

  @POST("{tasks}/{handle}")
  Call<StoreResponse> transformStore(
      @Path("tasks") String tasks,
      @Path("handle") String handle);

  // Using an external URL
  @Streaming
  @GET("{key}/{tasks}/{url}")
  Call<ResponseBody> transformExt(
      @Path("key") String key,
      @Path("tasks") String tasks,
      @Path("url") String url);

  @GET("{key}/debug/{tasks}/{url}")
  Call<JsonObject> transformDebugExt(
      @Path("key") String key,
      @Path("tasks") String tasks,
      @Path("url") String url);

  @POST("{key}/{tasks}/{url}")
  Call<StoreResponse> transformStoreExt(
      @Path("key") String key,
      @Path("tasks") String tasks,
      @Path("url") String url);
}
