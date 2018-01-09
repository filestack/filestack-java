package com.filestack.internal;

import com.filestack.FileLink;
import com.filestack.internal.responses.UploadResponse;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Function to be passed to {@link Flowable#create(FlowableOnSubscribe, BackpressureStrategy)}.
 * This class handles uploading of parts/chunks and makes calls to both S3 and Filestack endpoints.
 * An upload should be divided between multiple instances, with each uploading a subrange of parts.
 * We take a sectionIndex that tells us what area of the file to be responsible for.
 */
public class UploadTransferFunc implements FlowableOnSubscribe<Prog<FileLink>> {
  private FlowableEmitter<Prog<FileLink>> emitter;
  private Upload upload;
  private PartContainer container;

  UploadTransferFunc(Upload upload) {
    this.upload = upload;
  }

  @Override
  public void subscribe(FlowableEmitter<Prog<FileLink>> e) throws Exception {
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

    // Deprecated because MD5 is insecure not because this is unmaintained
    @SuppressWarnings("deprecation")
    HashCode hc = Hashing.md5().newHasher(size).putBytes(container.data, container.sent, size).hash();
    String md5 = BaseEncoding.base64().encode(hc.asBytes());

    final HashMap<String, RequestBody> params = new HashMap<>();
    params.putAll(upload.baseParams);
    params.put("part", Util.createStringPart(Integer.toString(container.num)));
    params.put("size", Util.createStringPart(Integer.toString(size)));
    params.put("md5", Util.createStringPart(md5));
    if (upload.intel) {
      params.put("offset", Util.createStringPart(Integer.toString(container.sent)));
    }

    RetryNetworkFunc<UploadResponse> func;
    func = new RetryNetworkFunc<UploadResponse>(5, 5, Upload.DELAY_BASE) {
      @Override
      Response<UploadResponse> work() throws Exception {
        return Networking.getUploadService().upload(params).execute();
      }
    };

    return func.call();
  }

  /** Upload chunk/part to S3. */
  private void uploadToS3() throws Exception {
    RetryNetworkFunc<Integer> func;

    func = new RetryNetworkFunc<Integer>(5, 5, Upload.DELAY_BASE) {
      private int size;

      @Override
      Response<ResponseBody> work() throws Exception {

        if (upload.intel) {
          size = Math.min(upload.getChunkSize(), container.size - container.sent);
        } else {
          size = Math.min(upload.partSize, container.size);
        }

        UploadResponse params = getUploadParams(size);
        Map<String, String> headers = params.getS3Headers();
        String url = params.getUrl();

        RequestBody requestBody = RequestBody.create(upload.mediaType, container.data, container.sent, size);
        return Networking.getUploadService().uploadS3(headers, url, requestBody).execute();
      }

      @Override
      public void onNetworkFail(int retries) throws Exception {
        upload.reduceChunkSize();
        super.onNetworkFail(retries);
      }

      @Override
      Integer process(Response response) {
        if (!upload.intel) {
          String etag = response.headers().get("ETag");
          upload.etags[container.num - 1] = etag;
        }
        container.sent += size;
        emitter.onNext(new Prog<FileLink>(size));
        return size;
      }
    };

    func.call();
  }

  /** For intelligent ingestion mode only. Called when all chunks of a part have been uploaded. */
  private void multipartCommit() throws Exception {
    final HashMap<String, RequestBody> params = new HashMap<>();
    params.putAll(upload.baseParams);
    params.put("part", Util.createStringPart(Integer.toString(container.num)));

    RetryNetworkFunc<ResponseBody> func;
    func = new RetryNetworkFunc<ResponseBody>(5, 5, Upload.DELAY_BASE) {
      @Override
      Response<ResponseBody> work() throws Exception {
        return Networking.getUploadService().commit(params).execute();
      }
    };

    func.call();
  }
}
