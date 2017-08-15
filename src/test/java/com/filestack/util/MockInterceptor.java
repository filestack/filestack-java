package com.filestack.util;

import static com.filestack.util.MockConstants.CDN_MOCK_CONTENT;
import static com.filestack.util.MockConstants.CDN_MOCK_FILENAME;
import static com.filestack.util.MockConstants.CODE_BAD_REQUEST;
import static com.filestack.util.MockConstants.CODE_FORBIDDEN;
import static com.filestack.util.MockConstants.CODE_NOT_FOUND;
import static com.filestack.util.MockConstants.CODE_OK;
import static com.filestack.util.MockConstants.HEADER_FILENAME;
import static com.filestack.util.MockConstants.MESSAGE_BAD_REQUEST;
import static com.filestack.util.MockConstants.MESSAGE_FORBIDDEN;
import static com.filestack.util.MockConstants.MESSAGE_NOT_FOUND;
import static com.filestack.util.MockConstants.MESSAGE_OK;
import static com.filestack.util.MockConstants.MIME_JSON;
import static com.filestack.util.MockConstants.MIME_TEXT;
import static com.filestack.util.MockConstants.MOCK_BASE_URL;
import static com.filestack.util.MockConstants.TEST_BAD_REQUEST_PATH;
import static com.filestack.util.MockConstants.TEST_FORBIDDEN_PATH;
import static com.filestack.util.MockConstants.TEST_HEADER_PATH;
import static com.filestack.util.MockConstants.TEST_NOT_FOUND_PATH;
import static com.filestack.util.MockConstants.TEST_UNMATCHED_PATH;

import com.filestack.errors.FilestackException;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Intercepts HTTP requests and returns mock responses for unit testing.
 */
public class MockInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    HttpUrl url = request.url();
    Response.Builder builder;

    String host = url.host();
    List<String> path = url.pathSegments();

    if (FilestackService.Cdn.URL.contains(host)) {
      builder = genCdnResponse(path.get(0));
    } else if (FilestackService.Api.URL.contains(host)) {
      builder = genApiResponse(request);
    } else if (FilestackService.Process.URL.contains(host)) {
      builder = genProcessResponse(request);
    } else if (MOCK_BASE_URL.contains(host)) {
      builder = genSimpleResponse(path.get(0));
    } else {
      throw new FilestackException("Host not mocked");
    }

    return builder
        .protocol(Protocol.HTTP_1_1)
        .request(chain.request())
        .build();
  }

  /**
   * Generates a test response for CDN requests.
   * Response contains data for a simple text file.
   *
   * @param handle File handle / id
   */
  private Response.Builder genCdnResponse(String handle) {
    String bodyText = String.format(CDN_MOCK_CONTENT, handle, new Date());
    ResponseBody body = ResponseBody.create(MediaType.parse(MIME_TEXT), bodyText);
    return new Response.Builder()
        .addHeader(HEADER_FILENAME, CDN_MOCK_FILENAME)
        .body(body)
        .code(CODE_OK)
        .message(MESSAGE_OK);
  }

  /**
   * Checks if an API call is well formed and sends an appropriate response.
   */
  private Response.Builder genApiResponse(Request request) throws IOException {
    String method = request.method();
    RequestBody requestBody = request.body();
    ResponseBody responseBody;

    switch (method) {

      // Overwrite endpoint: Just sanity checking the request
      case "POST":
        // We don't currently parse this response so we're not actually mocking it
        responseBody = ResponseBody.create(MediaType.parse(MIME_JSON), "");

        // This is the only checking we're doing for the overwrite request
        // Make sure we didn't send an empty body, shouldn't be possible
        if (request.body() == null) {
          return new Response.Builder()
              .code(CODE_BAD_REQUEST)
              .message(MESSAGE_BAD_REQUEST)
              .body(responseBody);
        }

        return new Response.Builder().code(CODE_OK).message(MESSAGE_OK).body(responseBody);

      // Delete endpoint: No checking, just send response
      case "DELETE":
        // We don't currently parse this response so we're not actually mocking it
        responseBody = ResponseBody.create(MediaType.parse(MIME_JSON), "");

        return new Response.Builder().code(CODE_OK).message(MESSAGE_OK).body(responseBody);

      default:
        throw new FilestackException("API method not mocked");
    }
  }

  private Response.Builder genProcessResponse(Request request) throws IOException {
    List<String> pathSegments = request.url().pathSegments();
    ResponseBody responseBody;

    if (pathSegments.contains("debug")) {
      // Generate a tiny subset of an actual response, just need something to convert
      JsonObject status = new JsonObject();
      status.addProperty("message", "OK");
      status.addProperty("http_code", 200);
      JsonObject content = new JsonObject();
      content.add("status", status);

      responseBody = ResponseBody.create(MediaType.parse(MIME_JSON), content.toString());

      // Always return a successful response to this endpoint
      return new Response.Builder().code(CODE_OK).message(MESSAGE_OK).body(responseBody);

    } else if (pathSegments.contains("store")) {
      JsonObject storeResponse = new JsonObject();
      storeResponse.addProperty("container", "my_bucket");
      storeResponse.addProperty("key", "NEW_HANDLE_some_file.jpg");
      storeResponse.addProperty("filename", "some_file.jpg");
      storeResponse.addProperty("type", "image/jpeg");
      storeResponse.addProperty("height", "1000");
      storeResponse.addProperty("width", "1000");
      storeResponse.addProperty("size", "200000");
      storeResponse.addProperty("url",
          "https://cdn.filestackcontent.com/NEW_HANDLE");
      responseBody = ResponseBody.create(MediaType.parse(MIME_JSON),
          storeResponse.toString());

      // Always return a successful response to this endpoint
      return new Response.Builder().code(CODE_OK).message(MESSAGE_OK).body(responseBody);

    } else {
      throw new FilestackException("Process method not mocked");
    }
  }

  /**
   * Generates simple responses for specific unit tests.
   */
  private Response.Builder genSimpleResponse(String path) throws IOException {
    switch (path) {
      case TEST_HEADER_PATH:
        return new Response.Builder().code(CODE_OK).message(MESSAGE_OK);
      case TEST_BAD_REQUEST_PATH:
        return new Response.Builder().code(CODE_BAD_REQUEST).message(MESSAGE_BAD_REQUEST);
      case TEST_FORBIDDEN_PATH:
        return new Response.Builder().code(CODE_FORBIDDEN).message(MESSAGE_FORBIDDEN);
      case TEST_NOT_FOUND_PATH:
        return new Response.Builder().code(CODE_NOT_FOUND).message(MESSAGE_NOT_FOUND);
      case TEST_UNMATCHED_PATH:
        return new Response.Builder().code(0).message("FAKE MESSAGE");
      default:
        throw new FilestackException("No path matched for mock host");
    }
  }
}
