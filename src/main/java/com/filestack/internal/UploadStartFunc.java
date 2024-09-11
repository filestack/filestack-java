package org.filestack.internal;

import org.filestack.internal.responses.StartResponse;
import io.reactivex.Flowable;

import java.util.concurrent.Callable;

/**
 * Function to be passed to {@link Flowable#fromCallable(Callable)}.
 * Handles initiating a multipart upload.
 */
public class UploadStartFunc implements Callable<Prog> {
  private final UploadService uploadService;
  private final Upload upload;
  
  UploadStartFunc(UploadService uploadService, Upload upload) {
    this.uploadService = uploadService;
    this.upload = upload;
  }

  @Override
  public Prog call() throws Exception {
    final long startTime = System.currentTimeMillis() / 1000;

    RetryNetworkFunc<StartResponse> func;
    func = new RetryNetworkFunc<StartResponse>(0, 5, Upload.DELAY_BASE) {
      @Override
      Response<StartResponse> work() throws Exception {
        return uploadService.start(upload.baseParams);
      }
    };

    StartResponse response = func.call();

    upload.baseParams.putAll(response.getUploadParams());

    if (upload.intel) {
      upload.intel = response.isIntelligent();
    }

    // If we tried to enable an intelligent upload and the response came back true
    // Then the account supports it and we perform an intelligent upload
    if (upload.intel) {
      upload.partSize = Upload.INTELLIGENT_PART_SIZE;
    // Otherwise we didn't enable it for this call or the account doesn't support it
    } else {
      upload.partSize = Upload.REGULAR_PART_SIZE;
      upload.baseParams.remove("multipart");
    }

    int numParts = (int) Math.ceil(upload.inputSize / (double) upload.partSize);
    upload.etags = new String[numParts];

    long endTime = System.currentTimeMillis() / 1000;
    return new Prog(startTime, endTime);
  }
}
