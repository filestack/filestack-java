package com.filestack.internal;

import com.filestack.Config;
import com.filestack.StorageOptions;
import com.filestack.internal.request.UploadRequest;
import com.filestack.internal.responses.UploadResponse;

import java.io.InputStream;

abstract class UploadTransferOperation {

  final String apiKey;
  final UploadService uploadService;
  final String uri;
  final String region;
  final String uploadId;
  final StorageOptions storageOptions;
  final InputStream inputStream;
  final int inputSize;

  UploadTransferOperation(String apiKey, UploadService uploadService, String uri, String region,
                                 String uploadId, StorageOptions storageOptions, InputStream inputStream,
                                 int inputSize) {
    this.apiKey = apiKey;
    this.uploadService = uploadService;
    this.uri = uri;
    this.region = region;
    this.uploadId = uploadId;
    this.storageOptions = storageOptions;
    this.inputStream = inputStream;
    this.inputSize = inputSize;
  }

  abstract String[] transfer() throws Exception;
  abstract UploadRequest buildUploadParamsRequest(int partNumber, int size, int offset, String encodedMd5);

  UploadResponse fetchUploadParams(byte[] data, int size, int partNumber, int offset) throws Exception {
    byte[] md5 = Hash.md5(data, offset, size);
    String encodedMd5 = Util.base64(md5);

    final UploadRequest uploadRequest = buildUploadParamsRequest(partNumber, size, offset, encodedMd5);

    RetryNetworkFunc<UploadResponse> func;
    func = new RetryNetworkFunc<UploadResponse>(5, 5, Upload.DELAY_BASE) {
      @Override
      Response<UploadResponse> work() throws Exception {
        return uploadService.upload(uploadRequest);
      }
    };

    return func.call();
  }

  public static class Factory {

    private final String apiKey;
    private final UploadService uploadService;
    private final String uri;
    private final String region;
    private final String uploadId;
    private final StorageOptions storageOptions;
    private final InputStream inputStream;
    private final int inputSize;

    Factory(Config config, UploadService uploadService, String uri, String region,
                                    String uploadId, StorageOptions storageOptions, InputStream inputStream,
                                    int inputSize) {
      this.apiKey = config.getApiKey();
      this.uploadService = uploadService;
      this.uri = uri;
      this.region = region;
      this.uploadId = uploadId;
      this.storageOptions = storageOptions;
      this.inputStream = inputStream;
      this.inputSize = inputSize;
    }

    public UploadTransferOperation create(boolean forIntelligentIngestion) {
      if (forIntelligentIngestion) {
        return new UploadTransferIntelligentOperation(apiKey, uploadService, uri, region, uploadId, storageOptions,
            inputStream, inputSize);
      }
      return new UploadTransferRegularOperation(apiKey, uploadService, uri, region, uploadId, storageOptions,
          inputStream, inputSize);
    }
  }
}
