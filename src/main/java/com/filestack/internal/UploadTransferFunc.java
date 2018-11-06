package com.filestack.internal;

import com.filestack.internal.request.CommitUploadRequest;
import com.filestack.internal.request.S3UploadRequest;
import com.filestack.internal.request.UploadRequest;
import com.filestack.internal.responses.UploadResponse;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.util.HashMap;

/**
 * Function to be passed to {@link Flowable#create(FlowableOnSubscribe, BackpressureStrategy)}.
 * This class handles uploading of parts/chunks and makes calls to both S3 and Filestack endpoints.
 * An upload should be divided between multiple instances.
 * Different instances are not assigned to section of the file, instead we synchronize reads.
 */
public class UploadTransferFunc implements FlowableOnSubscribe<Prog> {
  private final UploadService uploadService;
  private final Upload upload;
  private FlowableEmitter<Prog> emitter;
  private PartContainer container;

  private final String uri;
  private final String region;
  private final String uploadId;

  UploadTransferFunc(UploadService uploadService, Upload upload, String uri, String region, String uploadId) {
    this.uploadService = uploadService;
    this.upload = upload;
    this.uri = uri;
    this.region = region;
    this.uploadId = uploadId;
  }

  @Override
  public void subscribe(FlowableEmitter<Prog> e) throws Exception {
    emitter = e;
    container = new PartContainer(upload.partSize);

    while (upload.readInput(container) != -1) {
      while (container.sent != container.size) {
        uploadToS3();
      }
      if (upload.intel) {
        multipartCommit();
      }
    }
    emitter.onComplete();
  }

  /** Get parameters from Filestack for the upload to S3. */
  private UploadResponse getUploadParams(int size) throws Exception {
    byte[] md5 = Hash.md5(container.data, container.sent, size);
    String encodedMd5 = Util.base64(md5);

    final HashMap<String, RequestBody> params = new HashMap<>();
    params.putAll(upload.baseParams);
    params.put("part", Util.createStringPart(Integer.toString(container.num)));
    params.put("size", Util.createStringPart(Integer.toString(size)));
    params.put("md5", Util.createStringPart(encodedMd5));
    if (upload.intel) {
      params.put("offset", Util.createStringPart(Integer.toString(container.sent)));
    }

    final UploadRequest uploadRequest = new UploadRequest(
        upload.clientConf.getApiKey(),
        container.num,
        size,
        encodedMd5,
        uri,
        region,
        uploadId,
        upload.intel,
        upload.intel ? (long) container.sent : null
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

  /** Upload chunk/part to S3. */
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

        if (upload.intel) {
          size = Math.min(upload.getChunkSize(), container.size - container.sent);
        } else {
          size = Math.min(upload.partSize, container.size);
        }

        UploadResponse params = getUploadParams(size);
        S3UploadRequest uploadRequest = new S3UploadRequest(
            HttpUrl.parse(params.getUrl()),
            params.getS3Headers(),
            upload.storageOptions.getMimeType(),
            container.data,
            container.sent,
            size
        );
        return uploadService.uploadS3(uploadRequest);
      }

      @Override
      public void onNetworkFail(int retries) throws Exception {
        upload.reduceChunkSize();
        super.onNetworkFail(retries);
      }

      @Override
      ResponseBody process(Response<ResponseBody> response) {
        if (!upload.intel) {
          String etag = response.getHeaders().get("ETag");
          upload.etags[container.num - 1] = etag;
        }
        container.sent += size;
        long endTime = System.currentTimeMillis() / 1000;
        emitter.onNext(new Prog(startTime, endTime, size));
        return response.getData();
      }
    };

    func.call();
  }

  /** For intelligent ingestion mode only. Called when all chunks of a part have been uploaded. */
  private void multipartCommit() throws Exception {
    final CommitUploadRequest commitRequest = new CommitUploadRequest(
        upload.clientConf.getApiKey(),
        upload.inputSize,
        container.num,
        uri,
        region,
        uploadId,
        upload.storageOptions.getLocation()
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
