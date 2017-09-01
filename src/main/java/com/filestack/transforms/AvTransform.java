package com.filestack.transforms;

import com.filestack.FileLink;
import com.filestack.StorageOptions;
import com.filestack.errors.InternalException;
import com.filestack.errors.InvalidArgumentException;
import com.filestack.errors.InvalidParameterException;
import com.filestack.errors.PolicySignatureException;
import com.filestack.errors.ResourceNotFoundException;
import com.filestack.transforms.tasks.AvTransformOptions;
import com.filestack.util.Util;
import com.google.gson.JsonObject;
import io.reactivex.Single;
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
   * Gets converted content as a new {@link FileLink}. Starts processing on first call.
   * Returns null if still processing. Poll this method or use {@link #getFilelinkAsync()}.
   * If you need other data, such as thumbnails, use {@link Transform#getContentJson()}.
   *
   * @return null if processing, new {@link FileLink} if complete
   * @throws IOException               if request fails because of network or other IO issue
   * @throws PolicySignatureException  if security is missing or invalid or tagging isn't enabled
   * @throws ResourceNotFoundException if handle isn't found
   * @throws InvalidParameterException if handle is malformed
   * @throws InternalException         if unexpected error occurs
   */
  public FileLink getFilelink()
      throws IOException, PolicySignatureException, ResourceNotFoundException,
             InvalidParameterException, InternalException {

    JsonObject json = getContentJson();
    String status = json.get("status").getAsString();

    switch (status) {
      case "started":
      case "pending":
        return null;
      case "completed":
        JsonObject data = json.get("data").getAsJsonObject();
        String url = data.get("url").getAsString();
        String handle = url.split("/")[3];
        return new FileLink(apiKey, handle, security);
      default:
        throw new InternalException();
    }
  }

  // Async method wrappers

  /**
   * Asynchronously gets converted content as a new {@link FileLink}.
   * Uses default 10 second polling. Use {@link #getFilelinkAsync(int)} to adjust interval.
   *
   * @see #getFilelink()
   */
  public Single<FileLink> getFilelinkAsync() {
    return getFilelinkAsync(10);
  }

  /**
   * Asynchronously gets converted content as a new {@link FileLink}.
   *
   * @param pollInterval how frequently to poll (in seconds)
   * @see #getFilelink()
   */
  public Single<FileLink> getFilelinkAsync(final int pollInterval) {
    return Single.fromCallable(new Callable<FileLink>() {
      @Override
      public FileLink call() throws Exception {
        FileLink fileLink = null;
        while (fileLink == null) {
          fileLink = getFilelink();
          if (!Util.isUnitTest()) {
            Thread.sleep(pollInterval * 1000);
          }
        }
        return fileLink;
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
