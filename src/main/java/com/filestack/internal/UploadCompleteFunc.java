package com.filestack.internal;

import com.filestack.Config;
import com.filestack.FileLink;
import com.filestack.StorageOptions;
import com.filestack.internal.request.CompleteUploadRequest;
import com.filestack.internal.responses.CompleteResponse;

import java.util.concurrent.Callable;

/**
 * Handles completing a multipart upload, gets metadata for final file.
 * In intelligent ingestion mode the {@link UploadService#complete(CompleteUploadRequest)} call may return a
 * 202 response while the parts are still processing. In this case the {@link RetryNetworkFunc}
 * will handle it like a failure and automatically retry.
 */
public class UploadCompleteFunc implements Callable<Prog> {
  private final UploadService uploadService;
  private final String uri;
  private final String region;
  private final String uploadId;

  private final String[] etags;

  private final boolean intelligentUpload;
  private final StorageOptions storageOptions;
  private final int inputSize;
  private final Config config;

  public UploadCompleteFunc(UploadService uploadService, String uri, String region, String uploadId,
                            String[] etags, boolean intelligentUpload, StorageOptions storageOptions, int inputSize,
                            Config config) {
    this.uploadService = uploadService;
    this.uri = uri;
    this.region = region;
    this.uploadId = uploadId;
    this.etags = etags;
    this.intelligentUpload = intelligentUpload;
    this.storageOptions = storageOptions;
    this.inputSize = inputSize;
    this.config = config;
  }

  @Override
  public Prog call() throws Exception {
    final long startTime = System.currentTimeMillis() / 1000;
    final CompleteUploadRequest request;
    String apiKey = config.getApiKey();
    if (!intelligentUpload) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < etags.length; i++) {
        builder.append(i + 1).append(':').append(etags[i]).append(';');
      }
      builder.deleteCharAt(builder.length() - 1);
      request = CompleteUploadRequest.regular(
          apiKey,
          uri,
          region,
          uploadId,
          inputSize,
          builder.toString(),
          storageOptions
      );
    } else {
      request = CompleteUploadRequest.withIntelligentIngestion(
          apiKey,
          uri,
          region,
          uploadId,
          inputSize,
          storageOptions
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
    FileLink fileLink = new FileLink(config, response.getHandle());

    long endTime = System.currentTimeMillis() / 1000;
    return new Prog(startTime, endTime, fileLink);
  }
}
