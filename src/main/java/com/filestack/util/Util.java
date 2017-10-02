package com.filestack.util;

import com.filestack.HttpResponseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Response;

/**
 * Small helper functions that don't need their own class.
 */
public class Util {

  /**
   * Loads version string from properties file in resources folder.
   *
   * @return Version string
   */
  public static String getVersion() {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    InputStream inputStream = loader.getResourceAsStream("com/filestack/version.properties");
    Properties prop = new Properties();
    String version;

    try {
      prop.load(inputStream);
      version = prop.getProperty("version");
    } catch (IOException e) {
      version = "x.y.z";
    }

    return version;
  }

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
   * Checks status of backend responses.
   * Throws a {@link HttpResponseException} if response isn't in 200 range.
   *
   * @param response response from a backend call
   * @throws HttpResponseException on response code not in 200 range
   * @throws IOException           on error reading response body
   */
  public static void checkResponseAndThrow(Response response) throws IOException {
    if (response.isSuccessful()) {
      return;
    }

    ResponseBody errorBody = response.errorBody();
    if (errorBody != null) {
      throw new HttpResponseException(response.code(), errorBody.string());
    } else {
      throw new HttpResponseException(response.code());
    }
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
}
