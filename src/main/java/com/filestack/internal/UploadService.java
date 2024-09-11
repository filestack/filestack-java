package org.filestack.internal;

import org.filestack.internal.responses.CompleteResponse;
import org.filestack.internal.responses.StartResponse;
import org.filestack.internal.responses.UploadResponse;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Map;

/** Wraps endpoints that run on upload.filestackapi.com. */
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

  public Response<StartResponse> start(Map<String, RequestBody> parameters) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("multipart")
        .addPathSegment("start")
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(buildMultipartBody(parameters))
        .build();

    return networkClient.call(request, StartResponse.class);
  }

  public Response<UploadResponse> upload(Map<String, RequestBody> parameters) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("multipart")
        .addPathSegment("upload")
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(buildMultipartBody(parameters))
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

    Request request = new Request.Builder()
        .url(url)
        .post(buildMultipartBody(parameters))
        .build();

    return networkClient.call(request);
  }

  public Response<CompleteResponse> complete(Map<String, RequestBody> parameters) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("multipart")
        .addPathSegment("complete")
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(buildMultipartBody(parameters))
        .build();

    return networkClient.call(request, CompleteResponse.class);

  }

  private MultipartBody buildMultipartBody(Map<String, RequestBody> parameters) {
    MultipartBody.Builder multiPartBuilder = new MultipartBody.Builder()
        .setType(MultipartBody.FORM);

    for (Map.Entry<String, RequestBody> entry : parameters.entrySet()) {
      Headers headers = Headers.of(
          "Content-Disposition", "form-data; name=\"" + entry.getKey() + "\"",
          "Content-Transfer-Encoding", "binary");
      multiPartBuilder.addPart(headers, entry.getValue());
    }

    return multiPartBuilder.build();
  }
}
