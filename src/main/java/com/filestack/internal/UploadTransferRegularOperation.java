package com.filestack.internal;

import com.filestack.StorageOptions;
import com.filestack.internal.request.S3UploadRequest;
import com.filestack.internal.request.UploadRequest;
import com.filestack.internal.responses.UploadResponse;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

class UploadTransferRegularOperation extends UploadTransferOperation {

  private static final int REGULAR_PART_SIZE = 5 * 1024 * 1024;
  private static final int CONCURRENT_JOBS = 2;


  private final Executor executor = Executors.newFixedThreadPool(CONCURRENT_JOBS);
  private final String[] etags;

  UploadTransferRegularOperation(String apiKey, UploadService uploadService, String uri, String region,
                                 String uploadId, StorageOptions storageOptions, InputStream inputStream,
                                 int inputSize) {
    super(apiKey, uploadService, uri, region, uploadId, storageOptions, inputStream, inputSize);
    int numParts = (int) Math.ceil(inputSize / (double) REGULAR_PART_SIZE);
    this.etags = new String[numParts];
  }

  private Logger logger = Logger.getLogger(UploadTransferRegularOperation.class.getName());

  @Override
  String[] transfer() throws Exception {
    InputStream inputStream = new BufferedInputStream(this.inputStream);
    byte[] data = new byte[REGULAR_PART_SIZE];
    int read;
    int part = 0;
    final Semaphore semaphore = new Semaphore(CONCURRENT_JOBS);
    while ((read = inputStream.read(data)) != -1) {
      semaphore.acquire();
      logger.warning("transferring " + read);
      part++;
      final int fPart = part;
      final int fRead = read;
      final byte[] fData = Arrays.copyOf(data, data.length);
      executor.execute(new Runnable() {
        @Override
        public void run() {
          try {
            uploadToS3(fData, fRead, fPart);
            logger.warning("transferred:" + fRead);
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            semaphore.release();
          }
        }
      });


    }
    return etags;
  }

  private void updateEtags(int index, String value) {
    synchronized (etags) {
      etags[index] = value;
    }
  }

  @Override
  UploadRequest buildUploadParamsRequest(int partNumber, int size, int offset, String encodedMd5) {
    return new UploadRequest(apiKey, partNumber, size, encodedMd5, uri, region, uploadId, false, null);
  }

  private void uploadToS3(final byte[] data, final int size, final int part) throws Exception {
    RetryNetworkFunc<ResponseBody> func;
    logger.warning("part: " + part);
    func = new RetryNetworkFunc<ResponseBody>(5, 5, Upload.DELAY_BASE) {

      @Override
      Response<ResponseBody> work() throws Exception {
        UploadResponse params = fetchUploadParams(
            data,
            size,
            part,
            0
        );

        S3UploadRequest uploadRequest = new S3UploadRequest(
            HttpUrl.parse(params.getUrl()),
            params.getS3Headers(),
            storageOptions.getMimeType(),
            data,
            0,
            size
        );
        return uploadService.uploadS3(uploadRequest);
      }

      @Override
      ResponseBody process(Response<ResponseBody> response) {
        String etag = response.getHeaders().get("ETag");
        updateEtags(part - 1, etag);
        return response.getData();
      }
    };

    func.call();
  }
}
