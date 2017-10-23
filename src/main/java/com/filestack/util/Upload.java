package com.filestack.util;

import com.filestack.FsFile;
import com.filestack.FilestackClient;
import com.filestack.Progress;
import com.filestack.Security;
import com.filestack.StorageOptions;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/** Holds upload state and request logic. */
public class Upload {
  static final int CONCURRENCY = 4;
  static final int PROG_INTERVAL = 2;
  static final int MIN_CHUNK_SIZE = 32 * 1024;
  static final int DELAY_BASE = 2;

  final FsService fsService;
  final MediaType mediaType;
  final Security security;
  final String apiKey;
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
  public Upload(String path, boolean intelligent, StorageOptions options,
                FilestackClient fsClient, FsService fsService) {

    this.path = path;
    mediaType = options.getMediaType();
    apiKey = fsClient.getApiKey();
    security = fsClient.getSecurity();
    this.fsService = fsService;

    // Setup base parameters
    baseParams = new HashMap<>();
    baseParams.putAll(options.getAsPartMap());

    if (intelligent) {
      baseParams.put("multipart", Util.createStringPart("true"));
    }

    baseParams.put("apikey", Util.createStringPart(apiKey));

    if (security != null) {
      baseParams.put("policy", Util.createStringPart(security.getPolicy()));
      baseParams.put("signature", Util.createStringPart(security.getSignature()));
    }

    // Don't open the file here so that any exceptions with it get passed through the observable
    // Otherwise we'd have an async method that directly throws exceptions
  }

  /**
   * Start this upload asynchronously. Returns progress updates.
   *
   * @return {@link Flowable} that emits {@link Progress} events
   */
  public Flowable<Progress<FsFile>> runAsync() {
    Flowable<Prog<FsFile>> startFlow = Flowable
        .fromCallable(new UploadStartFunc(this))
        .subscribeOn(Schedulers.io());

    // Create multiple func instances to each upload a subrange of parts from the file
    // Merge each of these together into one so they're executed concurrently
    Flowable<Prog<FsFile>> transferFlow = Flowable.empty();
    for (int i = 0; i < CONCURRENCY; i++) {
      UploadTransferFunc func = new UploadTransferFunc(this, i);
      Flowable<Prog<FsFile>> temp = Flowable
          .create(func, BackpressureStrategy.BUFFER)
          .subscribeOn(Schedulers.io());
      transferFlow = transferFlow.mergeWith(temp);
    }

    Flowable<Prog<FsFile>> completeFlow = Flowable
        .fromCallable(new UploadCompleteFunc(this))
        .subscribeOn(Schedulers.io());

    return startFlow
        .concatWith(transferFlow)
        .concatWith(completeFlow)
        .buffer(PROG_INTERVAL, TimeUnit.SECONDS)
        .flatMap(new ProgMapFunc(this))
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
