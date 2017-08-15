package com.filestack.util;

import com.filestack.responses.CompleteResponse;
import com.filestack.responses.StartResponse;
import com.filestack.responses.UploadResponse;
import java.util.Map;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

/** Provides Filestack REST API endpoints for uploading. */
public interface FilestackUploadService {
  String URL = "https://upload.filestackapi.com/";

  @Multipart
  @POST("/multipart/start")
  Call<StartResponse> start(@PartMap Map<String, RequestBody> parameters);

  @Multipart
  @POST("/multipart/upload")
  Call<UploadResponse> upload(@PartMap Map<String, RequestBody> parameters);

  @PUT
  Call<ResponseBody> uploadS3(
      @HeaderMap Map<String, String> headers,
      @Url String url,
      @Body RequestBody body);

  @Multipart
  @POST("/multipart/commit")
  Call<ResponseBody> commit(@PartMap Map<String, RequestBody> parameters);

  @Multipart
  @POST("/multipart/complete")
  Call<CompleteResponse> complete(@PartMap Map<String, RequestBody> parameters);
}
