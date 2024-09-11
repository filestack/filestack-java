package org.filestack.internal;

import org.filestack.HttpException;
import com.google.gson.JsonObject;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.ByteString;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Small helper functions that don't need their own class.
 */
public class Util {

  /**
   * Creates {@link RequestBody Request Body} from String.
   * For multipart form uploads.
   */
  public static RequestBody createStringPart(String content) {
    return RequestBody.create(MultipartBody.FORM, content);
  }

  /**
   * Converts {@link RequestBody} to String.
   */
  public static String partToString(RequestBody body) throws IOException {
    Buffer buffer = new Buffer();
    body.writeTo(buffer);
    return buffer.readUtf8();
  }

  /**
   * Throws an {@link HttpException} with the code and error body from a {@link Response}.
   *
   * @param response response from a backend call
   * @throws HttpException always unless error reading response body
   * @throws IOException           on error reading response body
   */
  public static <T> void throwHttpResponseException(Response<T> response) throws IOException {
    ResponseBody errorBody = response.getErrorBody();
    if (errorBody != null) {
      throw new HttpException(response.code(), errorBody.string());
    } else {
      throw new HttpException(response.code());
    }
  }

  /**
   * Checks status of backend responses.
   * Throws a {@link HttpException} if response isn't in 200 range.
   *
   * @param response response from a backend call
   * @throws HttpException on response code not in 200 range
   * @throws IOException           on error reading response body
   */
  public static <T> void checkResponseAndThrow(Response<T> response) throws IOException {
    if (response.isSuccessful()) {
      return;
    }
    throwHttpResponseException(response);
  }

  /**
   * Creates and validates a new {@link File} for writing.
   *
   * @param pathname path to file
   * @return file pointing to pathname
   * @throws FileNotFoundException if path isn't usable
   * @throws IOException           if a file can't be created
   */
  public static File createWriteFile(String pathname) throws IOException {
    File file = new File(pathname);

    if (!file.createNewFile()) {
      if (file.isDirectory()) {
        throw new FileNotFoundException("Can't write to directory: " + file.getPath());
      } else if (!file.isFile()) {
        throw new FileNotFoundException("Can't write to special file: " + file.getPath());
      } else if (!file.canWrite()) {
        throw new FileNotFoundException("No write access: " + file.getAbsolutePath());
      }
    }

    return file;
  }

  /**
   * Creates and validates a new {@link File} for reading.
   *
   * @param pathname path to file
   * @return file pointing to pathname
   * @throws FileNotFoundException if path doesn't exist or isn't usable
   */
  public static File createReadFile(String pathname) throws IOException {
    File file = new File(pathname);

    if (!file.exists()) {
      throw new FileNotFoundException(file.getPath());
    } else if (file.isDirectory()) {
      throw new FileNotFoundException("Can't read from directory: " + file.getPath());
    } else if (!file.isFile()) {
      throw new FileNotFoundException("Can't read from special file: " + file.getPath());
    }

    return file;
  }

  public static boolean isUnitTest() {
    String env = System.getenv("TEST_TYPE");
    return env != null && env.equals("unit");
  }

  /** Check if String is null or empty. */
  public static boolean isNullOrEmpty(@Nullable String value) {
    return value == null || value.equals("");
  }

  /**
   * Throw an {@link java.lang.IllegalArgumentException} with message if String is mull or empty.
   */
  public static void throwIfNullOrEmpty(@Nullable String value, String message) {
    if (isNullOrEmpty(value)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static String base64(byte[] data) {
    return base64(data, 0, data.length);
  }

  public static String base64(byte[] data, int offset, int length) {
    return ByteString.of(data, offset, length).base64();
  }

  public static String base64Url(byte[] data) {
    return ByteString.of(data).base64Url();
  }

  /**
   * Populates {@link JsonObject} if value is not null.
   */
  public static void addIfNotNull(JsonObject object, String key, String value) {
    if (value != null) {
      object.addProperty(key, value);
    }
  }

  /**
   * Populates {@link JsonObject} if value is not null.
   */
  public static void addIfNotNull(JsonObject object, String key, @Nullable Number value) {
    if (value != null) {
      object.addProperty(key, value);
    }
  }


}
