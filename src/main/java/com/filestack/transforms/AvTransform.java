package com.filestack.transforms;

import com.filestack.FileLink;
import com.filestack.HttpResponseException;
import com.filestack.StorageOptions;
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
      throw new IllegalArgumentException("AvTransform can't be created without options");
    }

    if (storeOpts != null) {
      tasks.add(TransformTask.merge("video_convert", storeOpts.getAsTask(), avOps));
    } else {
      tasks.add(avOps);
    }
  }

  /**
   * Gets converted content as a new {@link FileLink}. Starts processing on first call.
   * Returns null if still processing. Poll this method or use {@link #getFileLinkAsync()}.
   * If you need other data, such as thumbnails, use {@link Transform#getContentJson()}.
   *
   * @return null if processing, new {@link FileLink} if complete
   * @throws HttpResponseException on error response from backend
   * @throws IOException           on network failure
   */
  public FileLink getFileLink() throws IOException {
    JsonObject json = getContentJson();
    String status = json.get("status").getAsString();

    switch (status) {
      case "started":
      case "pending":
        return null;
      case "completed":
        JsonObject data = json.get("data").getAsJsonObject();
        String url = data.get("url").getAsString();
        // Correcting for error where status is "completed" but fields are empty
        if (url.equals("")) {
          return null;
        }
        String handle = url.split("/")[3];
        return new FileLink(apiKey, handle, security);
      default:
        throw new IOException("Unexpected transform error: " + json.toString());
    }
  }

  // Async method wrappers

  /**
   * Asynchronously gets converted content as a new {@link FileLink}.
   * Uses default 10 second polling. Use {@link #getFileLinkAsync(int)} to adjust interval.
   *
   * @see #getFileLink()
   */
  public Single<FileLink> getFileLinkAsync() {
    return getFileLinkAsync(10);
  }

  /**
   * Asynchronously gets converted content as a new {@link FileLink}.
   *
   * @param pollInterval how frequently to poll (in seconds)
   * @see #getFileLink()
   */
  public Single<FileLink> getFileLinkAsync(final int pollInterval) {
    return Single.fromCallable(new Callable<FileLink>() {
      @Override
      public FileLink call() throws Exception {
        FileLink fileLink = null;
        while (fileLink == null) {
          fileLink = getFileLink();
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
