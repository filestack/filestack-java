package org.filestack.internal;

import org.filestack.internal.responses.StoreResponse;
import com.google.gson.JsonObject;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.io.IOException;

/** Wraps endpoints that run on cdn.filestackcontent.com. */
public class CdnService {

  private final HttpUrl apiUrl;
  private final NetworkClient networkClient;

  public CdnService(NetworkClient networkClient) {
    this(networkClient, HttpUrl.get("https://cdn.filestackcontent.com/"));
  }

  CdnService(NetworkClient networkClient, HttpUrl url) {
    this.networkClient = networkClient;
    this.apiUrl = url;
  }

  public Response<ResponseBody> get(String handle, String policy, String signature) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment(handle)
        .addQueryParameter("policy", policy)
        .addQueryParameter("signature", signature)
        .build();

    Request request = new Request.Builder()
        .url(url)
        .build();

    return networkClient.call(request);
  }

  public Response<ResponseBody> transform(String tasks, String handle) throws IOException {
    Request request = new Request.Builder()
        .url(transformUrl(tasks, handle))
        .build();

    return networkClient.call(request);
  }

  public HttpUrl transformUrl(String tasks, String handle) {
    return apiUrl.newBuilder()
        .addPathSegment(tasks)
        .addPathSegment(handle)
        .build();
  }

  public Response<JsonObject> transformDebug(String tasks, String handle) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("debug")
        .addPathSegment(tasks)
        .addPathSegment(handle)
        .build();

    Request request = new Request.Builder()
        .url(url)
        .build();

    return networkClient.call(request, JsonObject.class);
  }

  public Response<StoreResponse> transformStore(String tasks, String handle) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment(tasks)
        .addPathSegment(handle)
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(RequestBody.create(null, ""))
        .build();

    return networkClient.call(request, StoreResponse.class);
  }

  public Response<ResponseBody> transformExt(String key, String tasks, String url) throws IOException {
    Request request = new Request.Builder()
        .url(transformExtUrl(key, tasks, url))
        .build();

    return networkClient.call(request);
  }

  public HttpUrl transformExtUrl(String key, String tasks, String url) {
    return apiUrl.newBuilder()
        .addPathSegment(key)
        .addPathSegment(tasks)
        .addPathSegment(url)
        .build();
  }

  public Response<JsonObject> transformDebugExt(String key, String tasks, String url) throws IOException {
    HttpUrl httpUrl = apiUrl.newBuilder()
        .addPathSegment(key)
        .addPathSegment("debug")
        .addPathSegment(tasks)
        .addPathSegment(url)
        .build();

    Request request = new Request.Builder()
        .url(httpUrl)
        .build();

    return networkClient.call(request, JsonObject.class);
  }

  public Response<StoreResponse> transformStoreExt(String key, String tasks, String url) throws IOException {
    HttpUrl httpUrl = apiUrl.newBuilder()
        .addPathSegment(key)
        .addPathSegment(tasks)
        .addPathSegment(url)
        .build();

    Request request = new Request.Builder()
        .url(httpUrl)
        .post(RequestBody.create(null, ""))
        .build();

    return networkClient.call(request, StoreResponse.class);
  }
}
