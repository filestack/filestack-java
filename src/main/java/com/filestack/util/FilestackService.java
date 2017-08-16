package com.filestack.util;

import com.filestack.responses.StoreResponse;
import com.google.gson.JsonObject;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/** Provides Filestack REST API endpoints. */
public interface FilestackService {
  String URL = "https://cdn.filestackcontent.com/";

  // Base endpoints

  @GET("{handle}")
  @Streaming
  Call<ResponseBody> get(
      @Path("handle") String handle,
      @Query("policy") String policy,
      @Query("signature") String signature);

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

  // Transform endpoints

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
