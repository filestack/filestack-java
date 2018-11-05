package com.filestack.internal;

import com.filestack.internal.request.MultipartBodyBuilder;
import com.filestack.internal.request.StartUploadRequest;
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

  public Response<UploadResponse> upload(Map<String, RequestBody> parameters) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("multipart")
        .addPathSegment("upload")
        .build();

    MultipartBody multipartBody = new MultipartBodyBuilder()
        .addAll(parameters)
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(multipartBody)
        .build();

    return networkClient.call(request, UploadResponse.class);
  }

  public Response<ResponseBody> uploadS3(Map<String, String> headers, String url, RequestBody body) throws IOException {
    HttpUrl s3Url = HttpUrl.parse(url);
    if (s3Url == null) {
      throw new IOException("Invalid S3 url: " + url);
    }

    Headers.Builder headersBuilder = new Headers.Builder();
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      headersBuilder.add(entry.getKey(), entry.getValue());
    }

    Request request = new Request.Builder()
        .url(s3Url)
        .headers(headersBuilder.build())
        .put(body)
        .build();

    return networkClient.call(request);
  }

  public Response<ResponseBody> commit(Map<String, RequestBody> parameters) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("multipart")
        .addPathSegment("commit")
        .build();

    MultipartBody multipartBody = new MultipartBodyBuilder()
        .addAll(parameters)
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

}
