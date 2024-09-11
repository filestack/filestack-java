package org.filestack;

import org.filestack.internal.BaseService;
import org.filestack.internal.CdnService;
import org.filestack.internal.Networking;
import org.filestack.internal.Response;
import org.filestack.internal.Util;
import org.filestack.internal.responses.ImageTagResponse;
import org.filestack.transforms.AvTransform;
import org.filestack.transforms.ImageTransform;
import org.filestack.transforms.ImageTransformTask;
import org.filestack.transforms.tasks.AvTransformOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.Callable;

/** References and performs operations on an individual file. */
public class FileLink implements Serializable {
  protected final Config config;
  protected final String handle;

  private final CdnService cdnService;
  private final BaseService baseService;

  /**
   * Basic constructor for a FileLink.
   *
   * @deprecated FileLink objects should not be created by hand - use {@link Client#fileLink(String)} to acquire them.
   *     This constructor is scheduled to be removed in version 1.0.0.
   */
  @Deprecated
  public FileLink(Config config, String handle) {
    this.config = config;
    this.handle = handle;
    this.cdnService = Networking.getCdnService();
    this.baseService = Networking.getBaseService();
  }

  FileLink(Config config, CdnService cdnService, BaseService baseService, String handle) {
    this.config = config;
    this.handle = handle;
    this.cdnService = cdnService;
    this.baseService = baseService;
  }

  /**
   * Returns the content of a file.
   *
   * @return byte[] of file content
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   */
  public ResponseBody getContent() throws IOException {
    Response<ResponseBody> response = cdnService.get(this.handle, config.getPolicy(), config.getSignature());
    Util.checkResponseAndThrow(response);

    return response.getData();
  }

  /**
   * Saves the file using the name it was uploaded with.
   *
   * @see #download(String, String)
   */
  public File download(String directory) throws IOException {
    return download(directory, null);
  }

  /**
   * Saves the file overriding the name it was uploaded with.
   *
   * @param directory location to save the file in
   * @param filename  local name for the file
   * @throws HttpException on error response from backend
   * @throws IOException           on error creating file or network failure
   */
  public File download(String directory, @Nullable String filename) throws IOException {
    Response<ResponseBody> response = cdnService
        .get(this.handle, config.getPolicy(), config.getSignature());

    Util.checkResponseAndThrow(response);

    if (filename == null) {
      filename = response.getHeaders().get("x-file-name");
    }

    File file = Util.createWriteFile(directory + "/" + filename);

    ResponseBody body = response.getData();
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
   * @throws HttpException on error response from backend
   * @throws IOException           on error reading file or network failure
   */
  public void overwrite(String pathname) throws IOException {
    if (!config.hasSecurity()) {
      throw new IllegalStateException("Security must be set in order to overwrite");
    }

    File file = Util.createReadFile(pathname);

    String mimeType = URLConnection.guessContentTypeFromName(file.getName());
    RequestBody body = RequestBody.create(MediaType.parse(mimeType), file);

    Response<ResponseBody> response = baseService.overwrite(handle, config.getPolicy(), config.getSignature(), body);

    Util.checkResponseAndThrow(response);
  }

  /**
   * Deletes a file handle. Requires security to be set.
   *
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   */
  public void delete() throws IOException {
    if (!config.hasSecurity()) {
      throw new IllegalStateException("Security must be set in order to delete");
    }

    Response<ResponseBody> response =
        baseService.delete(handle, config.getApiKey(), config.getPolicy(), config.getSignature());

    Util.checkResponseAndThrow(response);
  }

  /**
   * Creates an {@link ImageTransform} object for this file.
   * A transformation call isn't made directly by this method.
   *
   * @return {@link ImageTransform ImageTransform} instance configured for this file
   */
  public ImageTransform imageTransform() {
    return new ImageTransform(config, cdnService, handle, false);
  }

  /**
   * Returns tags from the Google Vision API for image FileLinks.
   *
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   *
   * @see <a href="https://www.filestack.com/docs/tagging"></a>
   */
  public Map<String, Integer> imageTags() throws IOException {
    if (!config.hasSecurity()) {
      throw new IllegalStateException("Security must be set in order to tag an image");
    }

    ImageTransform transform = new ImageTransform(config, cdnService, handle, false);
    transform.addTask(new ImageTransformTask("tags"));
    JsonObject json = transform.getContentJson();
    Gson gson = new Gson();
    ImageTagResponse response = gson.fromJson(json, ImageTagResponse.class);
    return response.getAuto();
  }

  /**
   * Determines if an image FileLink is "safe for work" using the Google Vision API.
   *
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   *
   * @see <a href="https://www.filestack.com/docs/tagging"></a>
   */
  public boolean imageSfw() throws IOException {
    if (!config.hasSecurity()) {
      throw new IllegalStateException("Security must be set in order to tag an image");
    }

    ImageTransform transform = new ImageTransform(config, cdnService, handle, false);
    transform.addTask(new ImageTransformTask("sfw"));
    JsonObject json = transform.getContentJson();

    return json.get("sfw").getAsBoolean();
  }

  /**
   * Creates an {@link AvTransform} object for this file using default storage options.
   *
   * @see #avTransform(StorageOptions, AvTransformOptions)
   */
  public AvTransform avTransform(AvTransformOptions avOptions) {
    return avTransform(null, avOptions);
  }

  /**
   * Creates an {@link AvTransform} object for this file using custom storage options.
   * A transformation call isn't made directly by this method.
   * For both audio and video transformations.
   *
   * @param storeOptions options for how to save the file(s) in your storage backend
   * @param avOptions    options for how ot convert the file
   * @return {@link AvTransform ImageTransform} instance configured for this file
   */
  public AvTransform avTransform(@Nullable StorageOptions storeOptions, AvTransformOptions avOptions) {
    return new AvTransform(cdnService, config, handle, storeOptions, avOptions);
  }

  // Async methods
  // These just wrap each of the sync methods in some class of observable

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
    });
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
  public Single<File> downloadAsync(final String directory, @Nullable final String filename) {
    return Single.fromCallable(new Callable<File>() {
      @Override
      public File call() throws Exception {
        return download(directory, filename);
      }
    });
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
    });
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
    });
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
    });
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
    });
  }

  @Deprecated
  public Config getConfig() {
    return config;
  }

  public String getHandle() {
    return handle;
  }
}
