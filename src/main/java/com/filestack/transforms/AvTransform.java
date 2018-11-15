package com.filestack.transforms;

import com.filestack.Config;
import com.filestack.FileLink;
import com.filestack.HttpException;
import com.filestack.StorageOptions;
import com.filestack.internal.CdnService;
import com.filestack.internal.Networking;
import com.filestack.transforms.tasks.AvTransformOptions;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * {@link Transform Transform} subclass for audio and video transformations.
 */
public class AvTransform extends Transform {

  /**
   * Constructs new instance.
   *
   * @param config   should be from parent {@link FileLink}
   * @param handle   should be from parent {@link FileLink}
   * @param storeOps options for how to store the converted file
   * @param avOps    options for how to convert the file
   */
  public AvTransform(Config config, String handle, @Nullable StorageOptions storeOps,
                     @Nullable AvTransformOptions avOps) {

    super(Networking.getCdnService(), config, handle, false);

    if (avOps == null) {
      throw new IllegalArgumentException("AvTransform can't be created without options");
    }

    if (storeOps != null) {
      tasks.add(TransformTask.merge("video_convert", storeOps.getAsTask(), avOps));
    } else {
      tasks.add(avOps);
    }
  }

  /**
   * Constructs new instance.
   *
   * @param cdnService cdn client
   * @param config   should be from parent {@link FileLink}
   * @param handle   should be from parent {@link FileLink}
   * @param storeOps options for how to store the converted file
   * @param avOps    options for how to convert the file
   */
  public AvTransform(CdnService cdnService, Config config, String handle, @Nullable StorageOptions storeOps,
                     @Nullable AvTransformOptions avOps) {

    super(cdnService, config, handle, false);

    if (avOps == null) {
      throw new IllegalArgumentException("AvTransform can't be created without options");
    }

    if (storeOps != null) {
      tasks.add(TransformTask.merge("video_convert", storeOps.getAsTask(), avOps));
    } else {
      tasks.add(avOps);
    }
  }


  /**
   * Gets converted content as a new {@link FileLink}. Starts processing on first call.
   * Returns null if still processing.
   * If you need other data, such as thumbnails, use {@link Transform#getContentJson()}.
   *
   * @return null if processing, new {@link FileLink} if complete
   * @throws HttpException on error response from backend
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
        return new FileLink(config, handle);
      default:
        throw new IOException("Unexpected transform error: " + json.toString());
    }
  }
}
