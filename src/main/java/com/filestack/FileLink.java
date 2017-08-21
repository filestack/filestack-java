package com.filestack;

import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ResourceNotFoundException;
import com.filestack.errors.ValidationException;
import com.filestack.transforms.ImageTransform;
import com.filestack.util.FilestackService;
import com.filestack.util.Networking;

import com.filestack.util.Util;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Response;

/**
 * References and performs operations on an individual file.
 */
public class FileLink {
  private String apiKey;
  private String handle;
  private Security security;

  private FilestackService fsService;

  /**
   * Constructs an instance without security.
   *
   * @param apiKey account key from the dev portal
   * @param handle id for a file, first path segment in dev portal urls
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

    this.fsService = Networking.getFsService();
  }

  /**
   * Directly returns the content of a file.
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

    Response<ResponseBody> response = fsService.get(this.handle, policy, signature).execute();

    Util.checkResponseAndThrow(response);

    ResponseBody body = response.body();
    if (body == null) {
      throw new IOException();
    }

    return body.bytes();
  }

  /**
   * Saves the file to the specified directory using the name it was uploaded with.
   *
   * @param directory location to save the file in
   * @return {@link File File} object pointing to new file
   * @throws ValidationException       if the path (directory/filename) isn't writable
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if policy and/or signature are invalid or inadequate
   * @throws ResourceNotFoundException if handle isn't found
   * @throws InvalidParameterException if handle is malformed
   * @throws InternalException         if unexpected error occurs
   */
  public File download(String directory)
      throws ValidationException, IOException, PolicySignatureException,
             ResourceNotFoundException, InvalidParameterException, InternalException {
    return download(directory, null);
  }

  /**
   * Saves the file to the specified directory overriding the name it was uploaded with.
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

    Response<ResponseBody> response = fsService.get(this.handle, policy, signature).execute();

    Util.checkResponseAndThrow(response);

    if (filename == null) {
      filename = response.headers().get("x-file-name");
    }

    File file = new File(directory + "/" + filename);
    boolean created = file.createNewFile();

    if (!created) {
      if (Files.isDirectory(file.toPath())) {
        throw new ValidationException("Can't overwrite directory: " + file.getPath());
      } else if (!Files.isRegularFile(file.toPath())) {
        throw new ValidationException("Can't overwrite special file: " + file.getPath());
      } else if (!file.canWrite()) {
        throw new ValidationException("No write access: " + file.getAbsolutePath());
      }
    }

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
   * Replace the content of an existing file handle. Requires security to be set. Does not update
   * the filename or MIME type.
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

    File file = new File(pathname);
    if (!file.exists()) {
      throw new ValidationException("File doesn't exist: " + file.getPath());
    } else if (file.isDirectory()) {
      throw new ValidationException("Can't overwrite with directory: " + file.getPath());
    } else if (!Files.isRegularFile(file.toPath())) {
      throw new ValidationException("Can't overwrite with special file: " + file.getPath());
    }

    String mimeType = URLConnection.guessContentTypeFromName(file.getName());
    RequestBody body = RequestBody.create(MediaType.parse(mimeType), file);

    String policy = security.getPolicy();
    String signature = security.getSignature();

    Response response = fsService.overwrite(handle, policy, signature, body).execute();

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

    Response response = fsService.delete(handle, apiKey, policy, signature).execute();

    Util.checkResponseAndThrow(response);
  }

  // Async method wrappers

  /**
   * Async, observable version of {@link #getContent()}.
   * Same exceptions are passed through observable.
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
   * Async, observable version of {@link #download(String)}.
   * Same exceptions are passed through observable.
   */
  public Single<File> downloadAsync(final String directory) {
    return downloadAsync(directory, null);
  }

  /**
   * Async, observable version of {@link #download(String, String)}.
   * Same exceptions are passed through observable.
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
   * Async, observable version of {@link #overwrite(String)}.
   * Same exceptions are passed through observable.
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
   * Async, observable version of {@link #delete()}.
   * Same exceptions are passed through observable.
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
