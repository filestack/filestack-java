package org.filestack.internal;

import static org.filestack.UtilsKt.mockOkHttpResponse;

public class MockResponse {

  public static <T> Response<T> success(T data) {
    return Response.success(data, mockOkHttpResponse(200));
  }

  public static <T> Response<T> error(okhttp3.Response response) {
    return Response.error(response);
  }

  public static <T> Response<T> error() {
    return Response.error(mockOkHttpResponse(400));
  }


  public static <T> Response<T> error(int code) {
    return Response.error(mockOkHttpResponse(400));
  }

}
