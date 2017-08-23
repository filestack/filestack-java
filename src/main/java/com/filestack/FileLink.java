package com.filestack;

import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ResourceNotFoundException;
import com.filestack.errors.ValidationException;
import com.filestack.transforms.ImageTransform;
import com.filestack.util.FsApiService;
import com.filestack.util.FsCdnService;
import com.filestack.util.Networking;
import com.filestack.util.Util;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Response;

/** References and performs operations on an individual file. */
public class FileLink {
  private String apiKey;
  private String handle;
  private Security security;

  private FsApiService fsApiService;
  private FsCdnService fsCdnService;

  /**
   * Constructs an instance without security.
   *
   * @see #FileLink(String, String, Security)
   */
  public FileLink(String apiKey, String handle) {
    this(apiKey, handle, null);
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

    this.fsApiService = Networking.getFsApiService();
    this.fsCdnService = Networking.getFsCdnService();
  }

  /**
   * Returns the content of a file.
   *
   * @return byte[] of file content
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if policy and/or signature are invalid or inadequate
   * @throws ResourceNotFoundException if handle isn't found
   * @throws InvalidParameterException if handle is malformed
   * @throws InternalException         if unexpected error occurs
   */
  public byte[] getContent()
      throws IOException, PolicySignatureException, ResourceNotFoundException,
             InvalidParameterException, InternalException {

    String policy = security != null ? security.getPolicy() : null;
    String signature = security != null ? security.getSignature() : null;

    Response<ResponseBody> response = fsCdnService.get(this.handle, policy, signature).execute();

    Util.checkResponseAndThrow(response);

    ResponseBody body = response.body();
    if (body == null) {
      throw new IOException();
    }

    return body.bytes();
  }

  /**
   * Saves the file using the name it was uploaded with.
   *
   * @see #download(String, String)
   */
  public File download(String directory)
      throws ValidationException, IOException, PolicySignatureException,
             ResourceNotFoundException, InvalidParameterException, InternalException {
    return download(directory, null);
  }

  /**
   * Saves the file overriding the name it was uploaded with.
   *
   * @param directory location to save the file in
   * @param filename  local name for the file
   * @throws ValidationException       if the path (directory/filename) isn't writable
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if policy and/or signature are invalid or inadequate
   * @throws ResourceNotFoundException if handle isn't found
   * @throws InvalidParameterException if handle is malformed
   * @throws InternalException         if unexpected error occurs
   */
  public File download(String directory, String filename)
      throws ValidationException, IOException, PolicySignatureException,
             ResourceNotFoundException, InvalidParameterException, InternalException {

    String policy = security != null ? security.getPolicy() : null;
    String signature = security != null ? security.getSignature() : null;

    Response<ResponseBody> response = fsCdnService.get(this.handle, policy, signature).execute();

    Util.checkResponseAndThrow(response);

    if (filename == null) {
      filename = response.headers().get("x-file-name");
    }

    File file = Util.createWriteFile(directory + "/" + filename);

    ResponseBody body = response.body();
    if (body == null) {
      throw new IOException();
    }

    BufferedSource source = body.source();
    BufferedSink sink = Okio.buffer(Okio.sink(file));

    sink.writeAll(source);
    sink.close();

    return file;
  }

  /**
   * Replace the content of an existing file handle. Requires security to be set.
   * Does not update the filename or MIME type.
   *
   * @param pathname path to the file, can be local or absolute
   * @throws ValidationException       if security isn't set or the pathname is invalid
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if policy and/or signature are invalid or inadequate
   * @throws ResourceNotFoundException if handle isn't found
   * @throws InvalidParameterException if handle is malformed
   * @throws InternalException         if unexpected error occurs
   */
  public void overwrite(String pathname)
      throws ValidationException, IOException, PolicySignatureException,
             ResourceNotFoundException, InvalidParameterException, InternalException {

    if (security == null) {
      throw new ValidationException("Security must be set in order to overwrite");
    }

    File file = Util.createReadFile(pathname);

    String mimeType = URLConnection.guessContentTypeFromName(file.getName());
    RequestBody body = RequestBody.create(MediaType.parse(mimeType), file);

    String policy = security.getPolicy();
    String signature = security.getSignature();

    Response response = fsApiService.overwrite(handle, policy, signature, body).execute();

    Util.checkResponseAndThrow(response);
  }

  /**
   * Deletes a file handle. Requires security to be set.
   *
   * @throws ValidationException       if security isn't set
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if policy and/or signature are invalid or inadequate
   * @throws ResourceNotFoundException if handle isn't found
   * @throws InvalidParameterException if handle is malformed
   * @throws InternalException         if unexpected error occurs
   */
  public void delete()
      throws ValidationException, IOException, PolicySignatureException,
             ResourceNotFoundException, InvalidParameterException, InternalException {

    if (security == null) {
      throw new ValidationException("Security must be set in order to delete");
    }

    String policy = security.getPolicy();
    String signature = security.getSignature();

    Response response = fsApiService.delete(handle, apiKey, policy, signature).execute();

    Util.checkResponseAndThrow(response);
  }

  // Async method wrappers

  /**
   * Asynchronously returns the content of a file.
   *
   * @see #getContent()
   */
  public Single<byte[]> getContentAsync() {
    return Single.fromCallable(new Callable<byte[]>() {
      @Override
      public byte[] call() throws Exception {
        return getContent();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  /**
   * Asynchronously saves the file using the name it was uploaded with.
   *
   * @see #download(String, String)
   */
  public Single<File> downloadAsync(final String directory) {
    return downloadAsync(directory, null);
  }

  /**
   * Asynchronously saves the file overriding the name it was uploaded with.
   *
   * @see #download(String, String)
   */
  public Single<File> downloadAsync(final String directory, final String filename) {
    return Single.fromCallable(new Callable<File>() {
      @Override
      public File call() throws Exception {
        return download(directory, filename);
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  /**
   * Asynchronously replace the content of an existing file handle. Requires security to be set.
   * Does not update the filename or MIME type.
   *
   * @see #overwrite(String)
   */
  public Completable overwriteAsync(final String pathname) {
    return Completable.fromAction(new Action() {
      @Override
      public void run() throws Exception {
        overwrite(pathname);
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  /**
   * Asynchronously deletes a file handle. Requires security to be set.
   *
   * @see #delete()
   */
  public Completable deleteAsync() {
    return Completable.fromAction(new Action() {
      @Override
      public void run() throws Exception {
        delete();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  /**
   * Creates an {@link ImageTransform} object for this file.
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
