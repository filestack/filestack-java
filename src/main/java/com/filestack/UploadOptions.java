package com.filestack;

import com.filestack.util.Util;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Configures storage options for a new upload.
 */
public class UploadOptions {
  private HashMap<String, RequestBody> options = new HashMap<>();

  private UploadOptions() {
  }

  public Map<String, RequestBody> getMap() {
    return options;
  }

  // Javadoc comments adapted from
  // https://github.com/filepicker/filestack-uploads/blob/develop/README.md

  /**
   * Builds new {@link UploadOptions}.
   */
  public static class Builder {
    UploadOptions uploadOptions = new UploadOptions();

    /**
     * Set the location where the file will be stored. For locations other than s3 the file will be
     * first stored in Filestack's internal S3 bucket and then moved to a proper location.
     * If empty or not valid the file will be stored in S3 bucket configured for the selected
     * application (if configured) or in Filestack's internal S3 bucket.
     *
     * @param location s3, gcs, dropbox, azure, or rackspace
     */
    public Builder location(String location) {
      uploadOptions.options.put("store_location", Util.createStringPart(location));
      return this;
    }

    /**
     * Set an S3 region for the selected S3 bucket. If {@link #container(String)} is provided and
     * this is empty, the application will try to get the region from database or directly from
     * Amazon.
     */
    public Builder region(String region) {
      uploadOptions.options.put("store_region", Util.createStringPart(region));
      return this;
    }

    /** Set the name of the container where the file will be stored. */
    public Builder container(String container) {
      uploadOptions.options.put("store_container", Util.createStringPart(container));
      return this;
    }

    /**
     * Set the path where the file will be stored. By default, the file is stored at the root of the
     * container at a unique id, followed by an underscore, followed by the filename. Paths ending
     * with / will be treated as folders where the files will be stored in a similar way. Ignored
     * if the file is stored in Filestack's internal S3 bucket.
     */
    public Builder path(String path) {
      uploadOptions.options.put("store_path", Util.createStringPart(path));
      return this;
    }

    /**
     * Set if the file should be stored in a way that allows public access going directly to
     * the underlying file store. If empty or the file is stored in Filestack's internal S3 bucket
     * it defaults to private.
     *
     * @param access private or public
     */
    public Builder access(String access) {
      uploadOptions.options.put("store_access", Util.createStringPart(access));
      return this;
    }

    /**
     * Create the {@link UploadOptions} instance using the configured values.
     */
    public UploadOptions build() {
      if (!uploadOptions.options.containsKey("store_location")) {
        uploadOptions.options.put("store_location", Util.createStringPart("s3"));
      }
      return uploadOptions;
    }
  }
}
