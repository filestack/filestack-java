package com.filestack;

import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ResourceNotFoundException;
import com.filestack.errors.ValidationException;
import com.filestack.responses.ImageTagResponse;
import com.filestack.transforms.ImageTransform;
import com.filestack.transforms.ImageTransformTask;
import com.filestack.util.FsService;
import com.filestack.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Map;
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

  private FsService fsService;

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

    this.fsService = new FsService();
  }

  FileLink() {}

  /**
   * Builds new {@link FilestackClient}.
   */
  public static class Builder {
    private String apiKey;
    private String handle;
    private Security security;
    private FsService fsService;

    public Builder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public Builder handle(String handle) {
      this.handle = handle;
      return this;
    }

    public Builder security(Security security) {
      this.security = security;
      return this;
    }

    public Builder service(FsService fsService) {
      this.fsService = fsService;
      return this;
    }

    /**
     * Create the {@link FileLink} using the configured values.
     */
    public FileLink build() {
      FileLink fileLink = new FileLink();
      fileLink.apiKey = apiKey;
      fileLink.handle = handle;
      fileLink.security = security;
      fileLink.fsService = fsService != null ? fsService : new FsService();
      return fileLink;
    }
  }

  /**
   * Returns the content of a file.
   *
   * @return byte[] of file content
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if security is missing or invalid
   * @throws ResourceNotFoundException if handle isn't found
   * @throws InvalidParameterException if handle is malformed
   * @throws InternalException         if unexpected error occurs
   */
  public ResponseBody getContent()
      throws IOException, PolicySignatureException, ResourceNotFoundException,
             InvalidParameterException, InternalException {

    String policy = security != null ? security.getPolicy() : null;
    String signature = security != null ? security.getSignature() : null;

    Response<ResponseBody> response = fsService.get(this.handle, policy, signature).execute();

    Util.checkResponseAndThrow(response);

    return response.body();
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
   * @throws PolicySignatureException  if security is missing or invalid
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
   * @throws PolicySignatureException  if security is missing or invalid
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

    Response response = fsService.overwrite(handle, policy, signature, body).execute();

    Util.checkResponseAndThrow(response);
  }

  /**
   * Deletes a file handle. Requires security to be set.
   *
   * @throws ValidationException       if security isn't set
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if security is missing or invalid
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

  /**
   * Creates an {@link ImageTransform} object for this file.
   * A transformation call isn't made directly by this method.
   *
   * @return {@link ImageTransform ImageTransform} instance configured for this file
   */
  public ImageTransform imageTransform() {
    return new ImageTransform(this);
  }

  /**
   * Returns tags from the Google Vision API for image FileLinks.
   *
   * @throws ValidationException       if security isn't set
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if security is missing or invalid or tagging isn't enabled
   * @throws ResourceNotFoundException if handle isn't found
   * @throws InvalidParameterException if handle is malformed
   * @throws InternalException         if unexpected error occurs
   *
   * @see <a href="https://www.filestack.com/docs/tagging"></a>
   */
  public Map<String, Integer> imageTags()
      throws ValidationException, IOException, PolicySignatureException,
             ResourceNotFoundException, InvalidParameterException, InternalException {

    if (security == null) {
      throw new ValidationException("Security must be set in order to tag an image");
    }

    ImageTransform transform = new ImageTransform(this);
    transform.addTask(new ImageTransformTask("tags"));
    JsonObject json = transform.getContentJson();
    Gson gson = new Gson();
    ImageTagResponse response = gson.fromJson(json, ImageTagResponse.class);
    return response.getAuto();
  }

  /**
   * Determines if an image FileLink is "safe for work" using the Google Vision API.
   *
   * @throws ValidationException       if security isn't set
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if security is missing or invalid or tagging isn't enabled
   * @throws ResourceNotFoundException if handle isn't found
   * @throws InvalidParameterException if handle is malformed
   * @throws InternalException         if unexpected error occurs
   *
   * @see <a href="https://www.filestack.com/docs/tagging"></a>
   */
  public boolean imageSfw()
      throws ValidationException, IOException, PolicySignatureException,
      ResourceNotFoundException, InvalidParameterException, InternalException {

    if (security == null) {
      throw new ValidationException("Security must be set in order to tag an image");
    }

    ImageTransform transform = new ImageTransform(this);
    transform.addTask(new ImageTransformTask("sfw"));
    JsonObject json = transform.getContentJson();

    return json.get("sfw").getAsBoolean();
  }

  // Async method wrappers

  /**
   * Asynchronously returns the content of a file.
   *
   * @see #getContent()
   */
  public Single<ResponseBody> getContentAsync() {
    return Single.fromCallable(new Callable<ResponseBody>() {
      @Override
      public ResponseBody call() throws Exception {
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
   * Asynchronously returns tags from Google Vision API for image FileLinks.
   *
   * @see #imageTags()
   */
  public Single<Map<String, Integer>> imageTagsAsync() {
    return Single.fromCallable(new Callable<Map<String, Integer>>() {
      @Override
      public Map<String, Integer> call() throws Exception {
        return imageTags();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  /**
   * Asynchronously determines if an image FileLink is "safe for work" using the Google Vision API.
   *
   * @see #imageSfw()
   */
  public Single<Boolean> imageSfwAsync() {
    return Single.fromCallable(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return imageSfw();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  public String getHandle() {
    return handle;
  }

  public Security getSecurity() {
    return security;
  }

  public FsService getFsService() {
    return fsService;
  }
}
