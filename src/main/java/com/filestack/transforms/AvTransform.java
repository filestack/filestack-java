package com.filestack.transforms;

import com.filestack.FileLink;
import com.filestack.StorageOptions;
import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidArgumentException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ResourceNotFoundException;
import com.filestack.transforms.tasks.AvTransformOptions;
import com.google.gson.JsonObject;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * {@link Transform Transform} subclass for audio and video transformations.
 */
public class AvTransform extends Transform {

  /**
   * Constructs a new instance.
   *
   * @param fileLink  must point to an existing audio or video resource
   * @param storeOpts sets how the resulting file(s) are stored, uses defaults if null
   * @param avOps     sets conversion options
   */
  public AvTransform(FileLink fileLink, StorageOptions storeOpts, AvTransformOptions avOps) {

    super(fileLink);

    if (avOps == null) {
      throw new InvalidArgumentException("AvTransform can't be created without options");
    }

    if (storeOpts != null) {
      tasks.add(TransformTask.merge("video_convert", storeOpts.getAsTask(), avOps));
    } else {
      tasks.add(avOps);
    }
  }

  /**
   * Asynchronously starts the conversion.
   * Use {@link #isReady()} to check the status.
   * Once {@link #isReady()} returns true, use {@link #getFilelink()} to get the result.
   * If you need other data, such as thumbnails, use {@link Transform#getContentJson()}.
   */
  public void start() {
    Completable.fromAction(new Action() {
      @Override
      public void run() throws Exception {
        getContent();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  /**
   * Checks the status of the conversion.
   *
   * @return true if the file has finished processing
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if security is missing or invalid or tagging isn't enabled
   * @throws ResourceNotFoundException if handle isn't found
   * @throws InvalidParameterException if handle is malformed
   * @throws InternalException         if unexpected error occurs
   */
  public boolean isReady()
      throws IOException, PolicySignatureException, ResourceNotFoundException,
             InvalidParameterException, InternalException {

    return getContentJson().get("status").getAsString().equals("completed");
  }

  /**
   * Gets the converted content as a new {@link FileLink}.
   *
   * @return {@link FileLink} pointing to converted file
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if security is missing or invalid or tagging isn't enabled
   * @throws ResourceNotFoundException if handle isn't found
   * @throws InvalidParameterException if handle is malformed
   * @throws InternalException         if unexpected error occurs
   */
  public FileLink getFilelink()
      throws IOException, PolicySignatureException, ResourceNotFoundException,
             InvalidParameterException, InternalException {

    if (isReady()) {
      JsonObject json = getContentJson();
      String url = json.get("data").getAsJsonObject().get("url").getAsString();
      String handle = url.split("/")[3];
      return new FileLink(apiKey, handle, security);
    }

    return null;
  }

  // Async method wrappers

  /**
   * Asynchronously checks the status of the conversion.
   *
   * @see #isReady()
   */
  public Single<Boolean> isReadyAsync() {
    return Single.fromCallable(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return isReady();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }

  /**
   * Asynchronously gets the converted content as a new {@link FileLink}.
   *
   * @see #getFilelink()
   */
  public Single<FileLink> getFilelinkAsync() {
    return Single.fromCallable(new Callable<FileLink>() {
      @Override
      public FileLink call() throws Exception {
        return getFilelink();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
