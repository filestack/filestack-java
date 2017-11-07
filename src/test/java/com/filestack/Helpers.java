package com.filestack;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.mock.Calls;

public class Helpers {

  public static Call<ResponseBody> createRawCall(String contentType, String body) {
    MediaType mediaType = MediaType.parse(contentType);
    ResponseBody responseBody = ResponseBody.create(mediaType, body);
    return Calls.response(responseBody);
  }
}
