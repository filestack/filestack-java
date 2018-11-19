package com.filestack.internal;

import com.filestack.StorageOptions;
import com.filestack.internal.request.CommitUploadRequest;
import com.filestack.internal.request.S3UploadRequest;
import com.filestack.internal.request.UploadRequest;
import com.filestack.internal.responses.UploadResponse;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

class UploadTransferIntelligentOperation extends UploadTransferOperation {

  private static final int INTELLIGENT_PART_SIZE = 8 * 1024 * 1024;
  private static final int INITIAL_CHUNK_SIZE = 1024 * 1024;
  private static final int MIN_CHUNK_SIZE = 32 * 1024;

  private PartContainer container;
  private int chunkSize = INITIAL_CHUNK_SIZE;
  private int partIndex = 1;

  UploadTransferIntelligentOperation(String apiKey, UploadService uploadService, String uri, String region,
                                     String uploadId, StorageOptions storageOptions, InputStream inputStream,
                                     int inputSize) {
    super(apiKey, uploadService, uri, region, uploadId, storageOptions, inputStream, inputSize);
  }

  @Override
  String[] transfer() throws Exception {
    container = new PartContainer(INTELLIGENT_PART_SIZE);

    while (readInput(container) != -1) {
      while (container.sent != container.size) {
        uploadToS3();
      }
      multipartCommit();
    }
    return new String[0];
  }

  private void reduceChunkSize() throws IOException {
    chunkSize /= 2;
    if (chunkSize < MIN_CHUNK_SIZE) {
      throw new IOException();
    }
  }

  synchronized int readInput(PartContainer container) throws IOException {
    container.num = partIndex;
    container.size = inputStream.read(container.data);
    container.sent = 0;
    partIndex++;
    return container.size;
  }

  private void uploadToS3() throws Exception {
    RetryNetworkFunc<ResponseBody> func;

    func = new RetryNetworkFunc<ResponseBody>(5, 5, Upload.DELAY_BASE) {
      private int size;

      @Override
      Response<ResponseBody> work() throws Exception {

        size = Math.min(chunkSize, container.size - container.sent);

        UploadResponse params = fetchUploadParams(
            container.data,
            size,
            container.num,
            container.sent
        );

        S3UploadRequest uploadRequest = new S3UploadRequest(
            HttpUrl.parse(params.getUrl()),
            params.getS3Headers(),
            storageOptions.getMimeType(),
            container.data,
            container.sent,
            size
        );
        return uploadService.uploadS3(uploadRequest);
      }

      @Override
      public void onNetworkFail(int retries) throws Exception {
        reduceChunkSize();
        super.onNetworkFail(retries);
      }

      @Override
      ResponseBody process(Response<ResponseBody> response) {
        container.sent += size;
        return response.getData();
      }
    };

    func.call();
  }

  @Override
  UploadRequest buildUploadParamsRequest(int partNumber, int size, int offset, String encodedMd5) {
    return new UploadRequest(apiKey, partNumber, size, encodedMd5, uri, region, uploadId, true, (long) offset);
  }

  private void multipartCommit() throws Exception {
    final CommitUploadRequest commitRequest = new CommitUploadRequest(
        apiKey,
        inputSize,
        container.num,
        uri,
        region,
        uploadId,
        storageOptions.getLocation()
    );
    RetryNetworkFunc<ResponseBody> func;
    func = new RetryNetworkFunc<ResponseBody>(5, 5, Upload.DELAY_BASE) {
      @Override
      Response<ResponseBody> work() throws Exception {
        return uploadService.commit(commitRequest);
      }
    };

    func.call();
  }
}
