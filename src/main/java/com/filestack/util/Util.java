package com.filestack.util;

import com.filestack.errors.FilestackException;
import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ResourceNotFoundException;
import com.filestack.errors.ValidationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    String version = "";

    try {
      prop.load(inputStream);
    } catch (IOException e) {
      version = "x.y.z";
    }

    version = prop.getProperty("version");
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
   * Checks responses from any of the Filestack APIs.
   * Throws an appropriate exception based on the HTTP status code.
   *
   * @param response response from Filestack API
   * @throws InvalidParameterException on a 400 response
   * @throws PolicySignatureException  on a 403 response
   * @throws ResourceNotFoundException on a 404 response
   * @throws InternalException  on a 500 response
   */
  public static void checkResponseAndThrow(Response response)
      throws InvalidParameterException, PolicySignatureException, ResourceNotFoundException,
             InternalException {

    int code = response.code();

    if (code == 400) {
      throw new InvalidParameterException();
    } else if (code == 403) {
      throw new PolicySignatureException();
    } else if (code == 404) {
      throw new ResourceNotFoundException();
    } else if (code >= 500) {
      throw new InternalException();
    }
  }

  /**
   * Takes an {@link Throwable} and recasts it to one of the {@link FilestackException} classes.
   *
   * @param throwable generic object to cast and rethrow
   */
  public static void castExceptionAndThrow(Throwable throwable)
      throws InvalidParameterException, IOException, PolicySignatureException,
             ResourceNotFoundException, ValidationException, InternalException {

    if (throwable instanceof InvalidParameterException) {
      throw (InvalidParameterException) throwable;
    } else if (throwable instanceof IOException) {
      throw (IOException) throwable;
    } else if (throwable instanceof PolicySignatureException) {
      throw (PolicySignatureException) throwable;
    } else if (throwable instanceof ResourceNotFoundException) {
      throw (ResourceNotFoundException) throwable;
    } else if (throwable instanceof ValidationException) {
      throw (ValidationException) throwable;
    } else if (throwable instanceof  InternalException) {
      throw (InternalException) throwable;
    }

    throw new InternalException(throwable);
  }

  /**
   * Creates and validates a new {@link File} for writing.
   *
   * @param pathname path to file
   * @return file pointing to pathname
   * @throws ValidationException if the pathname can't be created or exists but is unusable
   */
  public static File createWriteFile(String pathname) throws ValidationException {
    File file = new File(pathname);

    boolean created;
    try {
      created = file.createNewFile();
    } catch (IOException e) {
      throw new ValidationException("Unable to create file: " + file.getPath(), e);
    }

    if (!created) {
      if (file.isDirectory()) {
        throw new ValidationException("Can't write to directory: " + file.getPath());
      } else if (!file.isFile()) {
        throw new ValidationException("Can't write to special file: " + file.getPath());
      } else if (!file.canWrite()) {
        throw new ValidationException("No write access: " + file.getAbsolutePath());
      }
    }

    return file;
  }

  /**
   * Creates and validates a new {@link File} for reading.
   *
   * @param pathname path to file
   * @return file pointing to pathname
   * @throws ValidationException if the pathname doesn't exist or isn't usable
   */
  public static File createReadFile(String pathname) throws ValidationException {
    File file = new File(pathname);

    if (!file.exists()) {
      throw new ValidationException("File doesn't exist: " + file.getPath());
    } else if (file.isDirectory()) {
      throw new ValidationException("Can't read from directory: " + file.getPath());
    } else if (!file.isFile()) {
      throw new ValidationException("Can't read from special file: " + file.getPath());
    }

    return file;
  }

  public static boolean isUnitTest() {
    String env = System.getenv("TEST_TYPE");
    return env != null && env.equals("unit");
  }
}
