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
import java.util.concurrent.*;
import java.util.logging.Logger;

class UploadTransferRegularOperation extends UploadTransferOperation {

  private static final int REGULAR_PART_SIZE = 5 * 1024 * 1024;
  private static final int CONCURRENT_JOBS = 2;

  private final String[] etags;

  UploadTransferRegularOperation(String apiKey, UploadService uploadService, String uri, String region,
                                 String uploadId, StorageOptions storageOptions, InputStream inputStream,
                                 int inputSize) {
    super(apiKey, uploadService, uri, region, uploadId, storageOptions, inputStream, inputSize);
    int numParts = (int) Math.ceil(inputSize / (double) REGULAR_PART_SIZE);
    this.etags = new String[numParts];
  }

  static {
    System.setProperty("java.util.logging.SimpleFormatter.format",
        "[%4$-7s] %5$s %n");
  }

  private Logger logger = Logger.getLogger(UploadTransferRegularOperation.class.getName());

  @Override
  String[] transfer() throws Exception {
    InputStream inputStream = new BufferedInputStream(this.inputStream);
    final byte[] buffer = new byte[REGULAR_PART_SIZE];
    ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_JOBS);
    CompletionService<UploadResult> completionService = new ExecutorCompletionService<>(executorService);
    final Semaphore semaphore = new Semaphore(CONCURRENT_JOBS);
    final CompletionListener completionListener = new CompletionListener(semaphore);
    for (int i = 0; i < etags.length; i++) {
      int read = inputStream.read(buffer);
      semaphore.acquire();
      UploadCallable uploadCallable = new UploadCallable(
          i + 1,
          Arrays.copyOf(buffer, read),
          completionListener
      );
      completionService.submit(uploadCallable);
    }
    Exception exception = null;
    for (int i = 0; i < etags.length; i++) {
      Future<UploadResult> uploadFuture = completionService.take();
      try {
        UploadResult uploadResult = uploadFuture.get();
        etags[uploadResult.partNum - 1] = uploadResult.eTag;
        log("Received tag %s for part %d", uploadResult.eTag, uploadResult.partNum);
      } catch (Exception e) {
        exception = e;
      }
    }
    return etags;
  }

  private void log(String template, Object... objects) {
    logger.info(String.format(template, objects));
  }

  @Override
  UploadRequest buildUploadParamsRequest(int partNumber, int size, int offset, String encodedMd5) {
    return new UploadRequest(apiKey, partNumber, size, encodedMd5, uri, region, uploadId, false, null);
  }

  private UploadResult uploadToS3(final byte[] data, final int size, final int part) throws Exception {
    RetryNetworkFunc<ResponseBody> func;

    func = new RetryNetworkFunc<ResponseBody>(5, 5, Upload.DELAY_BASE) {

      @Override
      Response<ResponseBody> work() throws Exception {
        log("Fetching upload params for part %d", part);
        long startTime = System.currentTimeMillis();
        UploadResponse params = fetchUploadParams(
            data,
            size,
            part,
            0
        );
        log("Acquired upload params for part %d in %dms", part, (System.currentTimeMillis() - startTime));

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
    };
    Response<ResponseBody> response = func.run();
    String tag = response.getHeaders().get("ETag");
    return new UploadResult(part, tag);
  }


  private class UploadCallable implements Callable<UploadResult> {

    private final int partNumber;
    private final byte[] data;
    private final CompletionListener completionListener;

    public UploadCallable(int partNumber, byte[] data, CompletionListener completionListener) {
      this.partNumber = partNumber;
      this.data = data;
      this.completionListener = completionListener;
    }

    @Override
    public UploadResult call() throws Exception {
      try {
        return uploadToS3(data, data.length, partNumber);
      } finally {
        completionListener.completed();
      }
    }
  }

  private static class UploadResult {
    final int partNum;
    final String eTag;

    public UploadResult(int partNum, String eTag) {
      this.partNum = partNum;
      this.eTag = eTag;
    }
  }

  private static class CompletionListener {
    private final Semaphore semaphore;

    public CompletionListener(Semaphore semaphore) {
      this.semaphore = semaphore;
    }

    void completed() {
      semaphore.release();
    }
  }
}
