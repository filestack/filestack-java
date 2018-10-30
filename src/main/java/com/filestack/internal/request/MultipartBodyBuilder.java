package com.filestack.internal.request;

import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class MultipartBodyBuilder {

  private final Map<String, RequestBody> parameters = new HashMap<>();

  public MultipartBodyBuilder add(String key, @Nullable String value) {
    if (value != null) {
      parameters.put(key, RequestBody.create(MultipartBody.FORM, value));
    }
    return this;
  }

  public MultipartBodyBuilder add(String key, long value) {
    parameters.put(key, RequestBody.create(MultipartBody.FORM, Long.toString(value)));
    return this;
  }

  public MultipartBodyBuilder addAll(Map<String, RequestBody> params) {
    parameters.putAll(params);
    return this;
  }

  public MultipartBody build() {
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
