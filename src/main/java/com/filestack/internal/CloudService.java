package org.filestack.internal;

import org.filestack.AppInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public class CloudService {

  private final HttpUrl apiUrl;
  private final NetworkClient networkClient;
  private final Gson gson;

  public CloudService(NetworkClient networkClient, Gson gson) {
    this(networkClient, gson, HttpUrl.get("https://cloud.filestackapi.com/"));
  }

  CloudService(NetworkClient networkClient, Gson gson, HttpUrl apiUrl) {
    this.apiUrl = apiUrl;
    this.networkClient = networkClient;
    this.gson = gson;
  }

  public Response<AppInfo> prefetch(JsonObject body) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("prefetch")
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(convert(body))
        .build();

    return networkClient.call(request, AppInfo.class);
  }

  public Response<JsonObject> list(JsonObject body) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("folder")
        .addPathSegment("list")
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(convert(body))
        .build();

    return networkClient.call(request, JsonObject.class);
  }

  public Response<JsonObject> store(JsonObject body) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .encodedPath("/store/")
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(convert(body))
        .build();

    return networkClient.call(request, JsonObject.class);
  }

  public Response<ResponseBody> logout(JsonObject body) throws IOException {
    HttpUrl url = apiUrl.newBuilder()
        .addPathSegment("auth")
        .addPathSegment("logout")
        .build();

    Request request = new Request.Builder()
        .url(url)
        .post(convert(body))
        .build();

    return networkClient.call(request);
  }

  private RequestBody convert(JsonObject body) throws IOException {
    TypeAdapter<JsonObject> adapter = gson.getAdapter(JsonObject.class);
    Buffer buffer = new Buffer();
    Writer writer = new OutputStreamWriter(buffer.outputStream(), Charset.forName("utf-8"));
    JsonWriter jsonWriter = gson.newJsonWriter(writer);
    adapter.write(jsonWriter, body);
    jsonWriter.close();
    return RequestBody.create(MediaType.get("application/json"), buffer.readByteString());
  }
}
