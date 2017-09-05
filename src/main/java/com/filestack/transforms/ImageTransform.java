package com.filestack.transforms;

import com.filestack.FileLink;
import com.filestack.FilestackClient;
import com.filestack.StorageOptions;
import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ResourceNotFoundException;
import com.filestack.responses.StoreResponse;
import com.filestack.util.Util;
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

  public ImageTransform(FileLink fileLink) {
    super(fileLink);
  }

  /**
   * Debugs the transformation as built so far, returning explanations of any issues.
   * @see <a href="https://www.filestack.com/docs/image-transformations/debug"></a>
   *
   * @return {@link JsonObject JSON} report for transformation
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if security is missing or invalid
   * @throws ResourceNotFoundException if API key, handle, or external URL are not found
   * @throws InvalidParameterException if a request parameter is missing or invalid
   * @throws InternalException         if unexpected error occurs
   */
  public JsonObject debug()
      throws IOException, PolicySignatureException, ResourceNotFoundException,
             InvalidParameterException, InternalException {

    String tasksString = getTasksString();

    Response<JsonObject> response;
    if (apiKey != null) {
      response = fsService.transformDebugExt(apiKey, tasksString, source).execute();
    } else {
      response = fsService.transformDebug(tasksString, source).execute();
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
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if security is missing or invalid
   * @throws ResourceNotFoundException if API key, handle, or external URL are not found
   * @throws InvalidParameterException if a request parameter is missing or invalid
   * @throws InternalException         if unexpected error occurs
   */
  public FileLink store()
      throws IOException, PolicySignatureException, ResourceNotFoundException,
             InvalidParameterException, InternalException {

    return store(null);
  }

  /**
   * Stores the result of a transformation into a new file.
   * @see <a href="https://www.filestack.com/docs/image-transformations/store"></a>
   *
   * @param storageOptions configure where and how your file is stored
   * @return new {@link FileLink FileLink} pointing to the file
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if security is missing or invalid
   * @throws ResourceNotFoundException if API key, handle, or external URL are not found
   * @throws InvalidParameterException if a request parameter is missing or invalid
   * @throws InternalException         if unexpected error occurs
   */
  public FileLink store(StorageOptions storageOptions)
      throws IOException, PolicySignatureException, ResourceNotFoundException,
             InvalidParameterException, InternalException {

    if (storageOptions == null) {
      storageOptions = new StorageOptions();
    }

    tasks.add(storageOptions.getAsTask());

    Response<StoreResponse> response;
    String tasksString = getTasksString();
    if (apiKey != null) {
      response = fsService.transformStoreExt(apiKey, tasksString, source).execute();
    } else {
      response = fsService.transformStore(tasksString, source).execute();
    }

    Util.checkResponseAndThrow(response);

    StoreResponse body = response.body();
    if (body == null) {
      throw new IOException();
    }

    String handle = body.getUrl().split("/")[3];
    return new FileLink(apiKey, handle, security);
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
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
