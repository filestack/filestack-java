package com.filestack.internal;

import com.filestack.Config;
import com.filestack.StorageOptions;
import com.filestack.internal.request.CommitUploadRequest;
import com.filestack.internal.request.S3UploadRequest;
import com.filestack.internal.request.UploadRequest;
import com.filestack.internal.responses.UploadResponse;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class handles uploading of parts/chunks and makes calls to both S3 and Filestack endpoints.
 * An upload should be divided between multiple instances.
 * Different instances are not assigned to section of the file, instead we synchronize reads.
 */
public class UploadTransferFunc {

  private static final int INITIAL_CHUNK_SIZE = 1024 * 1024;
  private static final int MIN_CHUNK_SIZE = 32 * 1024;

  private final UploadService uploadService;
  private final Upload upload;
  private PartContainer container;
  private final int numParts;

  private final InputStream inputStream;

  private int chunkSize = INITIAL_CHUNK_SIZE;

  private final String uri;
  private final String region;
  private final String uploadId;

  private final boolean intel;
  private final int partSize;

  private final StorageOptions storageOptions;

  private final int inputSize;

  private final String apiKey;
  private final String[] etags;

  private int partIndex = 1;

  UploadTransferFunc(UploadService uploadService, Upload upload, String uri, String region, String uploadId,
                     boolean intel, int partSize, StorageOptions storageOptions, int inputSize, Config config,
                     int numParts, InputStream inputStream) {
    this.uploadService = uploadService;
    this.upload = upload;
    this.uri = uri;
    this.region = region;
    this.uploadId = uploadId;
    this.intel = intel;
    this.partSize = partSize;
    this.storageOptions = storageOptions;
    this.inputSize = inputSize;
    this.apiKey = config.getApiKey();
    this.numParts = numParts;
    this.etags = new String[numParts];
    this.inputStream = inputStream;
  }

  public String[] run() throws Exception {
    container = new PartContainer(partSize);

    while (readInput(container) != -1) {
      while (container.sent != container.size) {
        uploadToS3();
      }
      if (intel) {
        multipartCommit();
      }
    }
    return etags;
  }

  private void reduceChunkSize() throws IOException {
    chunkSize /= 2;
    if (chunkSize < MIN_CHUNK_SIZE) {
      throw new IOException();
    }
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

  /**
   * Get parameters from Filestack for the upload to S3.
   */
  private UploadResponse getUploadParams(int size) throws Exception {
    byte[] md5 = Hash.md5(container.data, container.sent, size);
    String encodedMd5 = Util.base64(md5);

    final UploadRequest uploadRequest = new UploadRequest(
        apiKey,
        container.num,
        size,
        encodedMd5,
        uri,
        region,
        uploadId,
        intel,
        intel ? (long) container.sent : null
    );

    RetryNetworkFunc<UploadResponse> func;
    func = new RetryNetworkFunc<UploadResponse>(5, 5, Upload.DELAY_BASE) {
      @Override
      Response<UploadResponse> work() throws Exception {
        return uploadService.upload(uploadRequest);
      }
    };

    return func.call();
  }

  /**
   * Upload chunk/part to S3.
   */
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

        if (intel) {
          size = Math.min(chunkSize, container.size - container.sent);
        } else {
          size = Math.min(partSize, container.size);
        }

        UploadResponse params = getUploadParams(size);
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
        if (!intel) {
          String etag = response.getHeaders().get("ETag");
          etags[container.num - 1] = etag;
        }
        container.sent += size;
        return response.getData();
      }
    };

    func.call();
  }

  /**
   * For intelligent ingestion mode only. Called when all chunks of a part have been uploaded.
   */
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
