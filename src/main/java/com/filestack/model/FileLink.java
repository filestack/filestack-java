package com.filestack.model;

import com.filestack.model.transform.base.ImageTransform;
import com.filestack.util.FilestackException;
import com.filestack.util.FilestackService;
import com.filestack.util.Networking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import org.apache.tika.Tika;
import retrofit2.Response;

/**
 * References a file in Filestack.
 */
public class FileLink {
  private String apiKey;
  private String handle;
  private Security security;

  private FilestackService.Cdn cdnService;
  private FilestackService.Api apiService;

  /**
   * Constructs an instance without security.
   *
   * @param apiKey account key from the dev portal
   * @param handle id for a file, first path segment in dev portal urls
   */
  public FileLink(String apiKey, String handle) {
    this.apiKey = apiKey;
    this.handle = handle;

    this.cdnService = Networking.getCdnService();
    this.apiService = Networking.getApiService();
  }

  /**
   * Constructs an instance with security.
   *
   * @param apiKey   account key from the dev portal
   * @param handle   id for a file, first path segment in dev portal urls
   * @param security needs required permissions for your intended actions
   */
  public FileLink(String apiKey, String handle, Security security) {
    this.apiKey = apiKey;
    this.handle = handle;
    this.security = security;

    this.cdnService = Networking.getCdnService();
    this.apiService = Networking.getApiService();
  }

  /**
   * Directly returns the content of a file.
   *
   * @return raw {@link ResponseBody ResponseBody } containing the file content
   * @throws IOException for network failures, invalid handles, or invalid security
   */
  public ResponseBody getContent() throws IOException {
    if (security == null) {
      return cdnService.get(this.handle, null, null).execute().body();
    } else {
      return cdnService
          .get(this.handle, security.getPolicy(), security.getSignature())
          .execute()
          .body();
    }
  }

  /**
   * Saves the file to the specified directory using the name it was uploaded with.
   *
   * @param directory location to save the file in
   * @return {@link File File} object pointing to new file
   * @throws IOException for network failures, invalid handles, or invalid security
   */
  public File download(String directory) throws IOException {
    return download(directory, null);
  }

  /**
   * Saves the file to the specified directory overriding the name it was uploaded with.
   *
   * @param directory location to save the file in
   * @param filename  local name for the file
   * @return {@link File File} object pointing to new file
   * @throws IOException for network failures, invalid handles, or invalid security
   */
  public File download(String directory, String filename) throws IOException {
    Response<ResponseBody> response;

    if (security == null) {
      response = cdnService.get(this.handle, null, null).execute();
    } else {
      response = cdnService
          .get(this.handle, security.getPolicy(), security.getSignature())
          .execute();
    }

    if (filename == null) {
      filename = response.headers().get("x-file-name");
    }

    File file = new File(directory + "/" + filename);
    boolean created = file.createNewFile();

    BufferedSource source = response.body().source();
    BufferedSink sink = Okio.buffer(Okio.sink(file));

    sink.writeAll(source);
    sink.close();

    return file;
  }

  /**
   * Replace the content of an existing file handle.
   * Does not update the filename or MIME type.
   *
   * @param pathname path to the file, can be local or absolute
   * @throws IOException           for network failures, invalid handles, or invalid security
   * @throws FileNotFoundException if the given pathname isn't a file or doesn't exist
   */
  public void overwrite(String pathname) throws IOException {
    if (security == null) {
      throw new FilestackException("Overwrite requires security to be set");
    }

    File file = new File(pathname);
    if (!file.isFile()) {
      throw new FileNotFoundException(pathname);
    }

    Tika tika = new Tika();
    String mimeType = tika.detect(file);
    RequestBody body = RequestBody.create(MediaType.parse(mimeType), file);

    apiService.overwrite(handle, security.getPolicy(), security.getSignature(), body).execute();
  }

  /**
   * Deletes a file handle.
   * Requires security to be set.
   *
   * @throws IOException for network failures, invalid handles, or invalid security
   */
  public void delete() throws IOException {
    if (security == null) {
      throw new FilestackException("Delete requires security to be set");
    }

    apiService.delete(handle, apiKey, security.getPolicy(), security.getSignature()).execute();
  }

  /**
   * Creates an image transformation object for this file.
   * A transformation call isn't made directly by this method.
   *
   * @return {@link ImageTransform ImageTransform} instance configured for this file
   */
  public ImageTransform imageTransform() {
    return new ImageTransform(this);
  }

  public String getHandle() {
    return handle;
  }

  public Security getSecurity() {
    return security;
  }
}
