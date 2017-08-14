package com.filestack.util;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.junit.Assert;

/**
 * Generic interceptor that matches a URL regex, checks request body, and sends a JSON response.
 */
public class GenericInterceptor implements Interceptor {
  private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/response");

  private String urlRegex;
  private Map<String, String> params;
  private JsonObject response;

  private GenericInterceptor() {
    this.params = new HashMap<>();
    this.response = new JsonObject();
  }

  private void validateBody(Request request) throws IOException {
    RequestBody body = request.body();
    Assert.assertNotNull(body);
    Buffer buffer = new Buffer();
    body.writeTo(buffer);
    String bodyString = buffer.readUtf8();

    for (String key : params.keySet()) {
      String value = params.get(key);
      Assert.assertTrue("Missing key: " + key,
          bodyString.contains("name=\"" + key + "\""));
      if (value != null) {
        Assert.assertTrue("Missing key: " + value, bodyString.contains(value));
      }
    }
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    String url = request.url().toString();

    if (!url.matches(urlRegex)) {
      return chain.proceed(request);
    }

    validateBody(request);

    ResponseBody body = ResponseBody.create(JSON_MEDIA_TYPE, response.toString());

    return new Response.Builder()
        .protocol(Protocol.HTTP_1_1)
        .request(request)
        .code(200)
        .message("Okay")
        .body(body)
        .build();
  }

  public static class Builder {
    private GenericInterceptor interceptor = new GenericInterceptor();

    public Builder urlRegex(String urlRegex) {
      interceptor.urlRegex = urlRegex;
      return this;
    }

    /**
     * Add params that will just be checked for existence.
     */
    public Builder addEmptyParam(String... keys) {
      for (String key : keys) {
        interceptor.params.put(key, null);
      }
      return this;
    }

    public Builder addParam(String key, String value) {
      interceptor.params.put(key, value);
      return this;
    }

    /**
     * Add fields to the JSON response with default "test" values.
     */
    public Builder addEmptyResponse(String... keys) {
      for (String key : keys) {
        interceptor.response.addProperty(key, "test");
      }
      return this;
    }

    /**
     * Add a child JSON element to the response.
     * Each field of the child element will have default "test" values.
     *
     * @param key        for the element field
     * @param objectKeys for the element itself
     */
    public Builder addEmptyResponseObject(String key, String... objectKeys) {
      JsonObject object = new JsonObject();
      for (String objectKey : objectKeys) {
        object.addProperty(objectKey, "test");
      }
      interceptor.response.add(key, object);
      return this;
    }

    public Builder addResponse(String key, Object value) {
      interceptor.response.addProperty(key, value.toString());
      return this;
    }

    public Builder addResponse(String key, JsonObject object) {
      interceptor.response.add(key, object);
      return this;
    }

    public GenericInterceptor build() {
      return interceptor;
    }
  }
}
