package com.filestack.internal;

import com.filestack.FileLink;
import com.filestack.internal.request.CompleteUploadRequest;
import com.filestack.internal.responses.CompleteResponse;
import io.reactivex.Flowable;

import java.util.concurrent.Callable;

/**
 * Function to be passed to {@link Flowable#fromCallable(Callable)}.
 * Handles completing a multipart upload, gets metadata for final file.
 * In intelligent ingestion mode the {@link UploadService#complete(com.filestack.internal.request.CompleteUploadRequest)} call may return a
 * 202 response while the parts are still processing. In this case the {@link RetryNetworkFunc}
 * will handle it like a failure and automatically retry.
 */
public class UploadCompleteFunc implements Callable<Prog> {
  private final UploadService uploadService;
  private final Upload upload;

  private final String uri;
  private final String region;
  private final String uploadId;

  private final String[] etags;

  private final boolean intelligentUpload;

  public UploadCompleteFunc(UploadService uploadService, Upload upload, String uri, String region, String uploadId, String[] etags, boolean intelligentUpload) {
    this.uploadService = uploadService;
    this.upload = upload;
    this.uri = uri;
    this.region = region;
    this.uploadId = uploadId;
    this.etags = etags;
    this.intelligentUpload = intelligentUpload;
  }

  @Override
  public Prog call() throws Exception {
    final long startTime = System.currentTimeMillis() / 1000;
    final CompleteUploadRequest request;
    if (!intelligentUpload) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < etags.length; i++) {
        builder.append(i + 1).append(':').append(etags[i]).append(';');
      }
      builder.deleteCharAt(builder.length() - 1);
      request = CompleteUploadRequest.regular(
          upload.clientConf.getApiKey(),
          uri,
          region,
          uploadId,
          upload.inputSize,
          builder.toString(),
          upload.storageOptions
      );
    } else {
      request = CompleteUploadRequest.withIntelligentIngestion(
          upload.clientConf.getApiKey(),
          uri,
          region,
          uploadId,
          upload.inputSize,
          upload.storageOptions
      );
    }

    RetryNetworkFunc<CompleteResponse> func;
    func = new RetryNetworkFunc<CompleteResponse>(5, 5, Upload.DELAY_BASE) {

      @Override
      Response<CompleteResponse> work() throws Exception {
        return uploadService.complete(request);
      }
    };

    CompleteResponse response = func.call();
    FileLink fileLink = new FileLink(upload.clientConf, response.getHandle());

    long endTime = System.currentTimeMillis() / 1000;
    return new Prog(startTime, endTime, fileLink);
  }
}
