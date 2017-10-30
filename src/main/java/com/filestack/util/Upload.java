package com.filestack.util;

import com.filestack.Config;
import com.filestack.FileLink;
import com.filestack.Progress;
import com.filestack.StorageOptions;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** Holds upload state and request logic. */
public class Upload {
  static final int CONCURRENCY = 4;
  static final int PROG_INTERVAL = 2;
  static final int MIN_CHUNK_SIZE = 32 * 1024;
  static final int DELAY_BASE = 2;

  final Config config;
  final MediaType mediaType;
  final String path;

  boolean intelligent;
  int chunkSize = 1024 * 1024;
  int numParts;
  int partSize;
  int partsPerFunc;
  long filesize;
  Map<String, RequestBody> baseParams;
  String[] etags;

  /** Constructs new instance. */
  public Upload(Config config, String path, boolean intelligent, StorageOptions options) {
    this.config = config;
    this.path = path;
    mediaType = options.getMediaType();

    // Setup base parameters
    baseParams = new HashMap<>();
    baseParams.putAll(options.getAsPartMap());

    if (intelligent) {
      baseParams.put("multipart", Util.createStringPart("true"));
    }

    baseParams.put("apikey", Util.createStringPart(config.getApiKey()));

    if (config.hasSecurity()) {
      baseParams.put("policy", Util.createStringPart(config.getPolicy()));
      baseParams.put("signature", Util.createStringPart(config.getSignature()));
    }

    // Don't open the file here so that any exceptions with it get passed through the observable
    // Otherwise we'd have an async method that directly throws exceptions
  }

  /**
   * Start this upload asynchronously. Returns progress updates.
   *
   * @return {@link Flowable} that emits {@link Progress} events
   */
  public Flowable<Progress<FileLink>> runAsync() {
    Flowable<Prog<FileLink>> startFlow = Flowable
        .fromCallable(new UploadStartFunc(this))
        .subscribeOn(Schedulers.io());

    // Create multiple func instances to each upload a subrange of parts from the file
    // Merge each of these together into one so they're executed concurrently
    Flowable<Prog<FileLink>> transferFlow = Flowable.empty();
    for (int i = 0; i < CONCURRENCY; i++) {
      UploadTransferFunc func = new UploadTransferFunc(this, i);
      Flowable<Prog<FileLink>> temp = Flowable
          .create(func, BackpressureStrategy.BUFFER)
          .subscribeOn(Schedulers.io());
      transferFlow = transferFlow.mergeWith(temp);
    }

    Flowable<Prog<FileLink>> completeFlow = Flowable
        .fromCallable(new UploadCompleteFunc(this))
        .subscribeOn(Schedulers.io());

    return startFlow
        .concatWith(transferFlow)
        .concatWith(completeFlow)
        .buffer(PROG_INTERVAL, TimeUnit.SECONDS)
        .flatMap(new ProgMapFunc(this));
  }
}
