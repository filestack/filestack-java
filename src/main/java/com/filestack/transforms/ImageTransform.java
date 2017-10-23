package com.filestack.transforms;

import com.filestack.FsFile;
import com.filestack.FilestackClient;
import com.filestack.HttpResponseException;
import com.filestack.StorageOptions;
import com.filestack.util.Util;
import com.filestack.util.responses.StoreResponse;
import com.google.gson.JsonObject;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.concurrent.Callable;
import retrofit2.Response;

/**
 * {@link Transform Transform} subclass for image transformations.
 */
public class ImageTransform extends Transform {

  public ImageTransform(FilestackClient fsClient, String source) {
    super(fsClient, source);
  }

  public ImageTransform(FsFile fsFile) {
    super(fsFile);
  }

  /**
   * Debugs the transformation as built so far, returning explanations of any issues.
   * @see <a href="https://www.filestack.com/docs/image-transformations/debug"></a>
   *
   * @return {@link JsonObject JSON} report for transformation
   * @throws HttpResponseException on error response from backend
   * @throws IOException           on network failure
   */
  public JsonObject debug() throws IOException {
    String tasksString = getTasksString();

    Response<JsonObject> response;
    if (apiKey != null) {
      response = fsService.cdn().transformDebugExt(apiKey, tasksString, source).execute();
    } else {
      response = fsService.cdn().transformDebug(tasksString, source).execute();
    }

    Util.checkResponseAndThrow(response);

    JsonObject body = response.body();
    if (body == null) {
      throw new IOException();
    }

    return body;
  }

  /**
   * Stores the result of a transformation into a new file. Uses default storage options.
   * @see <a href="https://www.filestack.com/docs/image-transformations/store"></a>
   *
   * @return new {@link FsFile FsFile} pointing to the file
   * @throws HttpResponseException on error response from backend
   * @throws IOException           on network failure
   */
  public FsFile store() throws IOException {
    return store(null);
  }

  /**
   * Stores the result of a transformation into a new file.
   * @see <a href="https://www.filestack.com/docs/image-transformations/store"></a>
   *
   * @param storageOptions configure where and how your file is stored
   * @return new {@link FsFile FsFile} pointing to the file
   * @throws HttpResponseException on error response from backend
   * @throws IOException           on network failure
   */
  public FsFile store(StorageOptions storageOptions) throws IOException {
    if (storageOptions == null) {
      storageOptions = new StorageOptions();
    }

    tasks.add(storageOptions.getAsTask());

    Response<StoreResponse> response;
    String tasksString = getTasksString();
    if (apiKey != null) {
      response = fsService.cdn().transformStoreExt(apiKey, tasksString, source).execute();
    } else {
      response = fsService.cdn().transformStore(tasksString, source).execute();
    }

    Util.checkResponseAndThrow(response);

    StoreResponse body = response.body();
    if (body == null) {
      throw new IOException();
    }

    String handle = body.getUrl().split("/")[3];
    return new FsFile(apiKey, handle, security);
  }

  /**
   * Add a new transformation to the chain. Tasks are executed in the order they are added.
   *
   * @param task any of the available {@link ImageTransformTask} subclasses
   */
  public ImageTransform addTask(ImageTransformTask task) {
    if (task == null) {
      throw new NullPointerException("Cannot add null task to image transform");
    }
    tasks.add(task);
    return this;
  }

  // Async method wrappers

  /**
   * Async, observable version of {@link #debug()}.
   * Same exceptions are passed through observable.
   */
  public Single<JsonObject> debugAsync() {
    return Single.fromCallable(new Callable<JsonObject>() {
      @Override
      public JsonObject call() throws Exception {
        return debug();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  /**
   * Async, observable version of {@link #store()}.
   * Same exceptions are passed through observable.
   */
  public Single<FsFile> storeAsync() {
    return storeAsync(null);
  }

  /**
   * Async, observable version of {@link #store(StorageOptions)}.
   * Same exceptions are passed through observable.
   */
  public Single<FsFile> storeAsync(final StorageOptions storageOptions) {
    return Single.fromCallable(new Callable<FsFile>() {
      @Override
      public FsFile call() throws Exception {
        return store(storageOptions);
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
