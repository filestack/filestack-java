package com.filestack.internal;

import com.filestack.internal.request.*;
import com.filestack.internal.responses.CompleteResponse;
import com.filestack.internal.responses.StartResponse;
import com.filestack.internal.responses.UploadResponse;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public class UploadService {

  private final HttpUrl apiUrl;
  private final NetworkClient networkClient;

  public UploadService(NetworkClient networkClient) {
    this(networkClient, HttpUrl.get("https://upload.filestackapi.com/"));
  }

  UploadService(NetworkClient networkClient, HttpUrl url) {
    this.networkClient = networkClient;
    this.apiUrl = url;
  }

  public Response<StartResponse> start(StartUploadRequest uploadRequest) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("multipart")
        .addPathSegment("start")
        .build();

    MultipartBody body = new MultipartBodyBuilder()
        .add("apikey", uploadRequest.getApiKey())
        .add("size", uploadRequest.getSize())
        .add("multipart", uploadRequest.isIntelligentIngestion() ? "true" : null)
        .add("policy", uploadRequest.getPolicy())
        .add("signature", uploadRequest.getSignature())
        .add("filename", uploadRequest.getFilename())
        .add("mimetype", uploadRequest.getMimeType())
        .add("store_location", uploadRequest.getStoreLocation())
        .add("store_region", uploadRequest.getStoreRegion())
        .add("store_container", uploadRequest.getStoreContainer())
        .add("store_path", uploadRequest.getStorePath())
        .add("store_access", uploadRequest.getStoreAccess())
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();

    return networkClient.call(request, StartResponse.class);
  }

  public Response<UploadResponse> upload(UploadRequest uploadRequest) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("multipart")
        .addPathSegment("upload")
        .build();

    MultipartBody multipartBody = new MultipartBodyBuilder()
        .add("apikey", uploadRequest.getApiKey())
        .add("part", uploadRequest.getPart())
        .add("size", uploadRequest.getSize())
        .add("md5", uploadRequest.getMd5())
        .add("uri", uploadRequest.getUri())
        .add("region", uploadRequest.getRegion())
        .add("upload_id", uploadRequest.getUploadId())
        .add("multipart", uploadRequest.isIntelligentIngestion() ? "true" : null)
        .add("offset", uploadRequest.getOffset())
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(multipartBody)
        .build();

    return networkClient.call(request, UploadResponse.class);
  }

  public Response<ResponseBody> uploadS3(S3UploadRequest s3UploadRequest) throws IOException {
    Headers.Builder headersBuilder = new Headers.Builder();
    for (Map.Entry<String, String> entry : s3UploadRequest.getHeaders().entrySet()) {
      headersBuilder.add(entry.getKey(), entry.getValue());
    }

    RequestBody requestBody = RequestBody.create(
        MediaType.parse(s3UploadRequest.getMimeType()),
        s3UploadRequest.getData(),
        s3UploadRequest.getOffset(),
        s3UploadRequest.getByteCount()
    );

    Request request = new Request.Builder()
        .url(s3UploadRequest.getUrl())
        .headers(headersBuilder.build())
        .put(requestBody)
        .build();

    return networkClient.call(request);
  }

  public Response<ResponseBody> commit(CommitUploadRequest commitRequest) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("multipart")
        .addPathSegment("commit")
        .build();

    MultipartBody multipartBody = new MultipartBodyBuilder()
        .add("apikey", commitRequest.getApiKey())
        .add("uri", commitRequest.getUri())
        .add("region", commitRequest.getRegion())
        .add("upload_id", commitRequest.getUploadId())
        .add("size", commitRequest.getSize())
        .add("part", commitRequest.getPart())
        .add("store_location", commitRequest.getStoreLocation())
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(multipartBody)
        .build();

    return networkClient.call(request);
  }

  public Response<CompleteResponse> complete(Map<String, RequestBody> parameters) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("multipart")
        .addPathSegment("complete")
        .build();

    MultipartBody multipartBody = new MultipartBodyBuilder()
        .addAll(parameters)
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(multipartBody)
        .build();

    return networkClient.call(request, CompleteResponse.class);

  }

  public Response<CompleteResponse> complete(CompleteUploadRequest request) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("multipart")
        .addPathSegment("complete")
        .build();

    MultipartBodyBuilder builder = new MultipartBodyBuilder()
        .add("apikey", request.getApiKey())
        .add("uri", request.getUri())
        .add("region", request.getRegion())
        .add("upload_id", request.getUploadId())
        .add("filename", request.getFilename())
        .add("size", request.getSize())
        .add("mimetype", request.getMimeType())
        .add("store_location", request.getStoreLocation())
        .add("store_container", request.getStoreContainer())
        .add("store_path", request.getStorePath())
        .add("store_access", request.getStoreAccess())
        .add("store_region", request.getStoreRegion());

    if (request.isIntelligentIngestion()) {
      builder.add("multipart", "true");
    } else {
      builder.add("parts", request.getParts());
    }

    Request r = new Request.Builder()
        .url(url)
        .post(builder.build())
        .build();

    return networkClient.call(r, CompleteResponse.class);
  }
}
