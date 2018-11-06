package com.filestack.internal;

import com.filestack.internal.request.StartUploadRequest;
import com.filestack.internal.responses.StartResponse;
import io.reactivex.Flowable;

import java.util.concurrent.Callable;

/**
 * Function to be passed to {@link Flowable#fromCallable(Callable)}.
 * Handles initiating a multipart upload.
 */
public class UploadStartFunc implements Callable<StartResponse> {
  private final UploadService uploadService;
  private final Upload upload;
  
  @Override
  public StartResponse call() throws Exception {
    final StartUploadRequest startUploadRequest = new StartUploadRequest(
        upload.clientConf.getApiKey(),
        upload.inputSize,
        upload.intel,
        upload.clientConf.getPolicy(),
        upload.clientConf.getSignature(),
        upload.storageOptions
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

  UploadStartFunc(UploadService uploadService, Upload upload) {
    this.uploadService = uploadService;
    this.upload = upload;
  }

}
