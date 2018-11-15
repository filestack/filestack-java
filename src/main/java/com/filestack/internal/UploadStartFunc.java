package com.filestack.internal;

import com.filestack.Config;
import com.filestack.StorageOptions;
import com.filestack.internal.request.StartUploadRequest;
import com.filestack.internal.responses.StartResponse;

import java.util.concurrent.Callable;

public class UploadStartFunc implements Callable<StartResponse> {
  private final UploadService uploadService;
  private final boolean intelligentIngestion;
  private final StorageOptions storageOptions;
  private final int inputSize;
  private final Config config;

  @Override
  public StartResponse call() throws Exception {
    final StartUploadRequest startUploadRequest = new StartUploadRequest(
        config.getApiKey(),
        inputSize,
        intelligentIngestion,
        config.getPolicy(),
        config.getSignature(),
        storageOptions
    );

    RetryNetworkFunc<StartResponse> func;
    func = new RetryNetworkFunc<StartResponse>(0, 5, Upload.DELAY_BASE) {
      @Override
      Response<StartResponse> work() throws Exception {
        return uploadService.start(startUploadRequest);
      }
    };
    return func.call();
  }

  UploadStartFunc(UploadService uploadService, boolean intelligentIngestion,
                  StorageOptions storageOptions, int inputSize, Config config) {
    this.uploadService = uploadService;
    this.intelligentIngestion = intelligentIngestion;
    this.storageOptions = storageOptions;
    this.inputSize = inputSize;
    this.config = config;
  }

}
