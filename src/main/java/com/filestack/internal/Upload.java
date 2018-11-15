package com.filestack.internal;

import com.filestack.Config;
import com.filestack.FileLink;
import com.filestack.StorageOptions;
import com.filestack.internal.responses.StartResponse;

import java.io.InputStream;

/** Holds upload state and request logic. */
public class Upload {
  static final int DELAY_BASE = 2;
  private static final int INTELLIGENT_PART_SIZE = 8 * 1024 * 1024;
  private static final int REGULAR_PART_SIZE = 5 * 1024 * 1024;
  static final int CONCURRENCY = 4;

  private final UploadService uploadService;
  private final Config clientConf;
  private final int inputSize;
  private final StorageOptions storageOptions;
  private boolean intel;

  private final InputStream input;

  /** Constructs new instance. */
  public Upload(Config clientConf, UploadService uploadService, InputStream input, int inputSize, boolean intel,
                StorageOptions storeOpts) {
    this.clientConf = clientConf;
    this.uploadService = uploadService;
    this.input = input;
    this.inputSize = inputSize;
    this.intel = intel;
    this.storageOptions = storeOpts;
  }

  public FileLink upload() throws Exception {
    UploadStartFunc startFunc = new UploadStartFunc(uploadService, intel, storageOptions, inputSize, clientConf);
    StartResponse startResponse = startFunc.call();
    boolean intelligentIngestion = intel && startResponse.isIntelligent();
    final int partSize;
    if (intelligentIngestion) {
      partSize = Upload.INTELLIGENT_PART_SIZE;
    } else {
      partSize = Upload.REGULAR_PART_SIZE;
    }
    int numParts = (int) Math.ceil(inputSize / (double) partSize);
    UploadTransferFunc transferFunc = new UploadTransferFunc(
        uploadService,
        this,
        startResponse.getUri(),
        startResponse.getRegion(),
        startResponse.getUploadId(),
        intelligentIngestion,
        partSize,
        storageOptions,
        inputSize,
        clientConf,
        numParts,
        input);
    String[] etags = transferFunc.run();
    UploadCompleteFunc uploadCompleteFunc = new UploadCompleteFunc(
        uploadService, startResponse.getUri(), startResponse.getRegion(), startResponse.getUploadId(),
        etags, intelligentIngestion, storageOptions, inputSize, clientConf
    );
    return uploadCompleteFunc.call().getFileLink();
  }
}
