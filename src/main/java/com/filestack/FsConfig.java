package com.filestack;

import com.filestack.transforms.Transform;
import com.filestack.util.FsApiService;
import com.filestack.util.FsCdnService;
import com.filestack.util.FsCloudService;
import com.filestack.util.FsUploadService;
import com.filestack.util.Networking;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Holds config common to {@link FsClient}, {@link FsFile}, and {@link Transform} classes.
 */
public class FsConfig {
  protected FsApiService apiService;
  protected FsCdnService cdnService;
  protected FsCloudService cloudService;
  protected FsUploadService uploadService;
  protected final Scheduler subScheduler;
  protected final Scheduler obsScheduler;
  protected final String apiKey;
  protected final String policy;
  protected final String signature;

  /** Configures and builds new immutable instance. */
  @SuppressWarnings("unchecked")
  public static class Builder<T extends Builder<T>> {
    protected FsApiService apiService;
    protected FsCdnService cdnService;
    protected FsCloudService cloudService;
    protected FsUploadService uploadService;
    protected Scheduler subScheduler;
    protected Scheduler obsScheduler;
    protected String apiKey;
    protected String policy;
    protected String signature;

    public T apiService(FsApiService apiService) {
      this.apiService = apiService;
      return (T) this;
    }

    public T cdnService(FsCdnService cdnService) {
      this.cdnService = cdnService;
      return (T) this;
    }

    public T cloudService(FsCloudService cloudService) {
      this.cloudService = cloudService;
      return (T) this;
    }

    public T uploadService(FsUploadService uploadService) {
      this.uploadService = uploadService;
      return (T) this;
    }

    public T subScheduler(Scheduler subScheduler) {
      this.subScheduler = subScheduler;
      return (T) this;
    }

    public T obsScheduler(Scheduler obsScheduler) {
      this.obsScheduler = obsScheduler;
      return (T) this;
    }

    public T security(String policy, String signature) {
      this.policy = policy;
      this.signature = signature;
      return (T) this;
    }

    public T apiKey(String apiKey) {
      this.apiKey = apiKey;
      return (T) this;
    }

    /** Builds new instance, setting defaults for any null requirements. */
    public FsConfig build() {
      if (apiService == null) {
        apiService = Networking.getFsApiService();
      }
      if (cdnService == null) {
        cdnService = Networking.getFsCdnService();
      }
      if (cloudService == null) {
        cloudService = Networking.getFsCloudService();
      }
      if (uploadService == null) {
        uploadService = Networking.getFsUploadService();
      }
      if (subScheduler == null) {
        subScheduler = Schedulers.io();
      }
      if (obsScheduler == null) {
        obsScheduler = Schedulers.computation();
      }
      return new FsConfig(this);
    }
  }

  protected FsConfig(Builder<?> builder) {
    apiService = builder.apiService;
    cdnService = builder.cdnService;
    cloudService = builder.cloudService;
    uploadService = builder.uploadService;
    subScheduler = builder.subScheduler;
    obsScheduler = builder.obsScheduler;
    apiKey = builder.apiKey;
    policy = builder.policy;
    signature = builder.signature;
  }

  public FsApiService getApiService() {
    return apiService;
  }

  public FsCdnService getCdnService() {
    return cdnService;
  }

  public FsCloudService getCloudService() {
    return cloudService;
  }

  public FsUploadService getUploadService() {
    return uploadService;
  }

  public Scheduler getSubScheduler() {
    return subScheduler;
  }

  public Scheduler getObsScheduler() {
    return obsScheduler;
  }

  public String getApiKey() {
    return apiKey;
  }

  public boolean hasSecurity() {
    return policy != null && signature != null;
  }

  public String getPolicy() {
    return policy;
  }

  public String getSignature() {
    return signature;
  }
}
