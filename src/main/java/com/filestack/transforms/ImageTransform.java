package com.filestack.transforms;

import com.filestack.Config;
import com.filestack.FileLink;
import com.filestack.HttpException;
import com.filestack.StorageOptions;
import com.filestack.internal.Networking;
import com.filestack.internal.Util;
import com.filestack.internal.responses.StoreResponse;
import com.google.gson.JsonObject;
import io.reactivex.Single;
import retrofit2.Response;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * {@link Transform Transform} subclass for image transformations.
 */
public class ImageTransform extends Transform {

  public ImageTransform(Config config, String source, boolean isExternal) {
    super(config, source, isExternal);
  }

  /**
   * Debugs the transformation as built so far, returning explanations of any issues.
   * @see <a href="https://www.filestack.com/docs/image-transformations/debug"></a>
   *
   * @return {@link JsonObject JSON} report for transformation
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   */
  public JsonObject debug() throws IOException {
    String tasksString = getTasksString();

    Response<JsonObject> response;
    if (isExternal) {
      response = Networking.getCdnService()
          .transformDebugExt(config.getApiKey(), tasksString, source)
          .execute();
    } else {
      response = Networking.getCdnService()
          .transformDebug(tasksString, source)
          .execute();
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
   * @return new {@link FileLink FileLink} pointing to the file
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   */
  public FileLink store() throws IOException {
    return store(null);
  }

  /**
   * Stores the result of a transformation into a new file.
   * @see <a href="https://www.filestack.com/docs/image-transformations/store"></a>
   *
   * @param storageOptions configure where and how your file is stored
   * @return new {@link FileLink FileLink} pointing to the file
   * @throws HttpException on error response from backend
   * @throws IOException           on network failure
   */
  public FileLink store(StorageOptions storageOptions) throws IOException {
    if (storageOptions == null) {
      storageOptions = new StorageOptions();
    }

    tasks.add(storageOptions.getAsTask());

    Response<StoreResponse> response;
    String tasksString = getTasksString();
    if (isExternal) {
      response = Networking.getCdnService()
          .transformStoreExt(config.getApiKey(), tasksString, source)
          .execute();
    } else {
      response = Networking.getCdnService()
          .transformStore(tasksString, source)
          .execute();
    }

    Util.checkResponseAndThrow(response);

    StoreResponse body = response.body();
    if (body == null) {
      throw new IOException();
    }

    String handle = body.getUrl().split("/")[3];
    return new FileLink(config, handle);
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
    });
  }

  /**
   * Async, observable version of {@link #store()}.
   * Same exceptions are passed through observable.
   */
  public Single<FileLink> storeAsync() {
    return storeAsync(null);
  }

  /**
   * Async, observable version of {@link #store(StorageOptions)}.
   * Same exceptions are passed through observable.
   */
  public Single<FileLink> storeAsync(final StorageOptions storageOptions) {
    return Single.fromCallable(new Callable<FileLink>() {
      @Override
      public FileLink call() throws Exception {
        return store(storageOptions);
      }
    });
  }
}
