package com.filestack;

import com.filestack.transforms.Transform;
import com.filestack.util.FsService;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Holds config common to {@link FsClient}, {@link FsFile}, and {@link Transform} classes.
 */
public class FsConfig {
  protected final FsService service;
  protected final Scheduler subScheduler;
  protected final Scheduler obsScheduler;
  protected final String apiKey;
  protected final String policy;
  protected final String signature;

  /** Configures and builds new immutable instance. */
  @SuppressWarnings("unchecked")
  public static class Builder<T extends Builder<T>> {
    protected FsService fsService;
    protected Scheduler subScheduler;
    protected Scheduler obsScheduler;
    protected String apiKey;
    protected String policy;
    protected String signature;

    public T fsService(FsService fsService) {
      this.fsService = fsService;
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
      if (fsService == null) {
        fsService = new FsService();
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
    service = builder.fsService;
    subScheduler = builder.subScheduler;
    obsScheduler = builder.obsScheduler;
    apiKey = builder.apiKey;
    policy = builder.policy;
    signature = builder.signature;
  }

  public FsService getService() {
    return service;
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
