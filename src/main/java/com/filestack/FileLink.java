package com.filestack;

import com.filestack.errors.FilestackException;
import com.filestack.transforms.ImageTransform;
import com.filestack.util.FilestackService;
import com.filestack.util.Networking;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.FileNotFoundException;
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

/**
 * References and performs operations on an individual file.
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

    this.cdnService = Networking.getCdnService();
    this.apiService = Networking.getApiService();
  }

  /**
   * Directly returns the content of a file.
   *
   * @return byte[] of file content
   * @throws IOException for network failures, invalid handles, or invalid security
   */
  public byte[] getContent() throws IOException {
    String policy = security != null ? security.getPolicy() : null;
    String signature = security != null ? security.getSignature() : null;
    return cdnService.get(this.handle, policy, signature)
        .execute()
        .body()
        .bytes();
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
    String policy = security != null ? security.getPolicy() : null;
    String signature = security != null ? security.getSignature() : null;

    Response<ResponseBody> response = cdnService.get(this.handle, policy, signature).execute();

    if (filename == null) {
      filename = response.headers().get("x-file-name");
    }

    File file = new File(directory + "/" + filename);
    file.createNewFile();

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

    String mimeType = URLConnection.guessContentTypeFromName(file.getName());
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

  // Async method wrappers

  /**
   * Async, observable version of {@link #getContent()}.
   * Throws same exceptions.
   */
  public Single<byte[]> getContentAsync() {
    return Single.fromCallable(new Callable<byte[]>() {
      @Override
      public byte[] call() throws IOException {
        return getContent();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  /**
   * Async, observable version of {@link #download(String)}.
   * Throws same exceptions.
   */
  public Single<File> downloadAsync(final String directory) {
    return downloadAsync(directory, null);
  }

  /**
   * Async, observable version of {@link #download(String, String)}.
   * Throws same exceptions.
   */
  public Single<File> downloadAsync(final String directory, final String filename) {
    return Single.fromCallable(new Callable<File>() {
      @Override
      public File call() throws IOException {
        return download(directory, filename);
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  /**
   * Async, observable version of {@link #overwrite(String)}.
   * Throws same exceptions.
   */
  public Completable overwriteAsync(final String pathname) {
    return Completable.fromAction(new Action() {
      @Override
      public void run() throws IOException {
        overwrite(pathname);
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  /**
   * Async, observable version of {@link #delete()}.
   * Throws same exceptions.
   */
  public Completable deleteAsync() {
    return Completable.fromAction(new Action() {
      @Override
      public void run() throws IOException {
        delete();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
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
