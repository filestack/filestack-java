package com.filestack.util;

import com.filestack.FileLink;
import com.filestack.FilestackClient;
import com.filestack.Progress;
import com.filestack.Security;
import com.filestack.StorageOptions;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.reactivestreams.Publisher;

/** Holds upload state and request logic. */
public class Upload {
  static final int CONCURRENCY = 4;
  static final int PROG_INTERVAL = 2;
  static final int MIN_CHUNK_SIZE = 32 * 1024;

  final FsService fsService;
  final int delayBase;
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

  public Upload(String path, String contentType, StorageOptions options, boolean intelligent,
                int delayBase, FilestackClient fsClient, FsService fsService) {

    this.path = path;
    mediaType = MediaType.parse(contentType);
    this.delayBase = delayBase;
    apiKey = fsClient.getApiKey();
    security = fsClient.getSecurity();
    this.fsService = fsService;

    // Setup base parameters
    baseParams = new HashMap<>();
    baseParams.put("mimetype", Util.createStringPart(contentType));
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
        .flatMap(new Function<List<Prog<FileLink>>, Publisher<Progress<FileLink>>>() {
          private long startTime = System.currentTimeMillis();
          private long bytesSent;
          private double avgRate; // bytes / second
          private double oldAvgRate;

          @Override
          public Publisher<Progress<FileLink>> apply(List<Prog<FileLink>> progs)
              throws Exception {
            long currentTime = System.currentTimeMillis();
            int elapsed = (int) ((currentTime - startTime) / 1000L);

            // Skip update if buffer is empty
            if (progs.size() == 0) {
              return Flowable.empty();
            }

            int bytes = 0;
            FileLink data = null;
            for (Prog<FileLink> simple : progs) {
              bytes += simple.getBytes();
              data = simple.getData();
            }

            // Bytes could equal 0 if we only have a status from start or complete func
            // We don't want to update the rate for requests that don't carry file content
            if (bytes != 0) {
              bytesSent += bytes;
              if (avgRate == 0) {
                avgRate = bytes;
              } else {
                avgRate = Progress.calcAvg(bytes, avgRate);
              }
            }

            // Skip update if we haven't sent anything or are waiting on the complete func
            if (bytesSent == 0 || (bytesSent / filesize == 1 && data == null)) {
              return Flowable.empty();
            }

            oldAvgRate = avgRate;

            double rate = avgRate / PROG_INTERVAL; // Want bytes / second not bytes / interval
            return Flowable.just(new Progress<>(bytesSent, filesize, elapsed, rate, data));
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
