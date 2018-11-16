package com.filestack.internal;

import com.filestack.StorageOptions;
import com.filestack.internal.request.S3UploadRequest;
import com.filestack.internal.request.UploadRequest;
import com.filestack.internal.responses.UploadResponse;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

class UploadTransferRegularOperation extends UploadTransferOperation {

  private static final int REGULAR_PART_SIZE = 5 * 1024 * 1024;

  private PartContainer container;


  private final int partSize = REGULAR_PART_SIZE;
  private final String[] etags;

  private int partIndex = 1;

  UploadTransferRegularOperation(String apiKey, UploadService uploadService, String uri, String region,
                                 String uploadId, StorageOptions storageOptions, InputStream inputStream,
                                 int inputSize) {
    super(apiKey, uploadService, uri, region, uploadId, storageOptions, inputStream, inputSize);
    int numParts = (int) Math.ceil(inputSize / (double) partSize);
    this.etags = new String[numParts];
  }

  @Override
  String[] transfer() throws Exception {
    container = new PartContainer(partSize);

    while (readInput(container) != -1) {
      while (container.sent != container.size) {
        uploadToS3();
      }
    }
    return etags;
  }

  @Override
  UploadRequest buildUploadParamsRequest(int partNumber, int size, int offset, String encodedMd5) {
    return new UploadRequest(apiKey, partNumber, size, encodedMd5, uri, region, uploadId, false, null);
  }
  /**
   * Read from the input stream into a simple container object. Synchronized to support concurrent
   * worker threads. The part object should be created once and reused to keep mem usage and garbage
   * collection down.
   */
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
      private long startTime;

      @Override
      Response<ResponseBody> work() throws Exception {

        if (startTime == 0) {
          startTime = System.currentTimeMillis() / 1000;
        }

        size = Math.min(partSize, container.size);

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
      ResponseBody process(Response<ResponseBody> response) {
        String etag = response.getHeaders().get("ETag");
        etags[container.num - 1] = etag;
        container.sent += size;
        return response.getData();
      }
    };

    func.call();
  }
}
