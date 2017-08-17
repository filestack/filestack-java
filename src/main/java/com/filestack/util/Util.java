package com.filestack.util;

import com.filestack.errors.FilestackException;
import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ResourceNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
   * Takes an {@link Exception} and recasts it to one of the {@link FilestackException} classes.
   *
   * @param e generic object to cast and rethrow
   */
  public static void castExceptionAndThrow(Exception e)
      throws InvalidParameterException, IOException, PolicySignatureException,
             ResourceNotFoundException, InternalException {

    if (e instanceof InvalidParameterException) {
      throw (InvalidParameterException) e;
    } else if (e instanceof IOException) {
      throw (IOException) e;
    } else if (e instanceof PolicySignatureException) {
      throw (PolicySignatureException) e;
    } else if (e instanceof ResourceNotFoundException) {
      throw (ResourceNotFoundException) e;
    } else if (e instanceof  InternalException) {
      throw (InternalException) e;
    }

    throw new InternalException(e);
  }
}
