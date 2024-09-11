package org.filestack.internal;

import okhttp3.Headers;
import okhttp3.ResponseBody;

import javax.annotation.Nullable;

public class Response<T> {

  static <T> Response<T> success(T body, okhttp3.Response response) {
    return new Response<>(body, response, null);
  }

  static <T> Response<T> error(okhttp3.Response response) {
    return new Response<>(null, response, response.body());
  }

  private final okhttp3.Response rawResponse;
  private final T data;
  private final ResponseBody errorBody;

  private Response(@Nullable T data, okhttp3.Response rawResponse, @Nullable ResponseBody errorBody) {
    this.data = data;
    this.rawResponse = rawResponse;
    this.errorBody = errorBody;
  }

  public boolean isSuccessful() {
    return rawResponse.isSuccessful();
  }

  public int code() {
    return rawResponse.code();
  }

  @Nullable
  public T getData() {
    return data;
  }

  @Nullable
  public ResponseBody getErrorBody() {
    return errorBody;
  }

  public Headers getHeaders() {
    return rawResponse.headers();
  }

}
