package com.filestack.internal;

import com.filestack.Config;
import com.filestack.FileLink;
import com.filestack.StorageOptions;
import com.filestack.internal.responses.StartResponse;

import java.io.InputStream;

public class Upload {
  static final int DELAY_BASE = 0;
  static final int CONCURRENCY = 4;

  private final UploadService uploadService;
  private final Config clientConf;
  private final int inputSize;
  private final StorageOptions storageOptions;
  private boolean intel;

  private final InputStream input;

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

    String uri = startResponse.getUri();
    String region = startResponse.getRegion();
    String uploadId = startResponse.getUploadId();


    UploadTransferOperation.Factory factory = new UploadTransferOperation.Factory(
        clientConf,
        uploadService,
        uri,
        region,
        uploadId,
        storageOptions,
        input,
        inputSize
    );
    UploadTransferOperation operation = factory.create(intelligentIngestion);
    final String[] etags = operation.transfer();

    UploadCompleteFunc uploadCompleteFunc = new UploadCompleteFunc(
        uploadService, uri, region, uploadId,
        etags, intelligentIngestion, storageOptions, inputSize, clientConf
    );
    return uploadCompleteFunc.call().getFileLink();
  }
}
