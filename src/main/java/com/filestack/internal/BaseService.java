package org.filestack.internal;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.io.IOException;

/** Wraps endpoints that run on www.filestackapi.com. */
public class BaseService {

  private final NetworkClient networkClient;
  private final HttpUrl url;

  public BaseService(NetworkClient networkClient) {
    this(networkClient, HttpUrl.get("https://www.filestackapi.com/api/file/"));
  }

  BaseService(NetworkClient networkClient, HttpUrl url) {
    this.networkClient = networkClient;
    this.url = url;
  }

  public Response<ResponseBody> overwrite(String handle, String policy, String signature,
                                          RequestBody body) throws IOException {
    HttpUrl url = this.url.newBuilder()
        .addPathSegment(handle)
        .addQueryParameter("policy", policy)
        .addQueryParameter("signature", signature)
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();

    return networkClient.call(request);
  }

  public Response<ResponseBody> delete(String handle, String key, String policy,
                                       String signature) throws IOException {
    HttpUrl url = this.url.newBuilder()
        .addPathSegment(handle)
        .addQueryParameter("key", key)
        .addQueryParameter("policy", policy)
        .addQueryParameter("signature", signature)
        .build();

    Request request = new Request.Builder()
        .url(url)
        .delete()
        .build();

    return networkClient.call(request);
  }
}
