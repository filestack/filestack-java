package com.filestack;

import com.filestack.transforms.Transform;
import com.filestack.internal.BaseService;
import com.filestack.internal.CdnService;
import com.filestack.internal.CloudService;
import com.filestack.internal.UploadService;
import com.filestack.internal.Networking;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Holds config common to {@link Client}, {@link FileLink}, and {@link Transform} classes.
 */
public class Config {
  protected BaseService baseService;
  protected CdnService cdnService;
  protected CloudService cloudService;
  protected UploadService uploadService;
  protected final Scheduler subScheduler;
  protected final Scheduler obsScheduler;
  protected final String apiKey;
  protected final String policy;
  protected final String signature;

  /** Configures and builds new immutable instance. */
  @SuppressWarnings("unchecked")
  public static class Builder<T extends Builder<T>> {
    protected BaseService apiService;
    protected CdnService cdnService;
    protected CloudService cloudService;
    protected UploadService uploadService;
    protected Scheduler subScheduler;
    protected Scheduler obsScheduler;
    protected String apiKey;
    protected String policy;
    protected String signature;

    public T apiService(BaseService apiService) {
      this.apiService = apiService;
      return (T) this;
    }

    public T cdnService(CdnService cdnService) {
      this.cdnService = cdnService;
      return (T) this;
    }

    public T cloudService(CloudService cloudService) {
      this.cloudService = cloudService;
      return (T) this;
    }

    public T uploadService(UploadService uploadService) {
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

    /** Sets policy and signature to be used for all requests. */
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
    public Config build() {
      if (apiService == null) {
        apiService = Networking.getBaseService();
      }
      if (cdnService == null) {
        cdnService = Networking.getCdnService();
      }
      if (cloudService == null) {
        cloudService = Networking.getCloudService();
      }
      if (uploadService == null) {
        uploadService = Networking.getUploadService();
      }
      if (subScheduler == null) {
        subScheduler = Schedulers.io();
      }
      if (obsScheduler == null) {
        obsScheduler = Schedulers.computation();
      }
      return new Config(this);
    }
  }

  protected Config(Builder<?> builder) {
    baseService = builder.apiService;
    cdnService = builder.cdnService;
    cloudService = builder.cloudService;
    uploadService = builder.uploadService;
    subScheduler = builder.subScheduler;
    obsScheduler = builder.obsScheduler;
    apiKey = builder.apiKey;
    policy = builder.policy;
    signature = builder.signature;
  }

  public BaseService getBaseService() {
    return baseService;
  }

  public CdnService getCdnService() {
    return cdnService;
  }

  public CloudService getCloudService() {
    return cloudService;
  }

  public UploadService getUploadService() {
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
