package com.filestack.util;

import com.filestack.FileLink;
import com.filestack.responses.UploadResponse;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Function to be passed to {@link Flowable#create(FlowableOnSubscribe, BackpressureStrategy)}.
 * This class handles uploading of parts/chunks and makes calls to both S3 and Filestack endpoints.
 * An upload should be divided between multiple instances, with each uploading a subrange of parts.
 * We take a sectionIndex that tells us what area of the file to be responsible for.
 */
public class UploadTransferFunc implements FlowableOnSubscribe<Prog<FileLink>> {
  private Upload upload;
  private int sectionIndex;

  UploadTransferFunc(Upload upload, int sectionIndex) {
    this.upload = upload;
    this.sectionIndex = sectionIndex;
  }

  @Override
  public void subscribe(FlowableEmitter<Prog<FileLink>> e) throws Exception {
    int start = sectionIndex * upload.partsPerFunc;
    int count = Math.min(upload.partsPerFunc, upload.numParts - start);

    if (count <= 0) {
      // No work for this instance
      e.onComplete();
    }

    RandomAccessFile file = new RandomAccessFile(upload.path, "r");
    file.seek(start * upload.partSize);

    byte[] bytes;
    if (upload.intelligent) {
      bytes = new byte[upload.chunkSize];
    } else {
      bytes = new byte[upload.partSize];
    }

    int bytesLeft;
    int bytesRead;
    int bytesSent;
    int offset;
    int part;

    // Loop through parts assigned to this thread
    for (int i = 0; i < count; i++) {
      bytesLeft = upload.partSize;
      offset = 0;
      part = start + i + 1;

      // Loop through bytes of a single part
      // If standard multipart upload, we upload in one partSize chunk
      // If intelligent ingestion upload, we upload in multiple chunkSize chunks
      while (bytesLeft != 0) {

        if (upload.intelligent) {
          bytesRead = file.read(bytes, 0, upload.chunkSize);
        } else {
          bytesRead = file.read(bytes, 0, upload.partSize);
        }

        if (bytesRead == -1) {
          break;
        }

        bytesSent = uploadToS3(upload, part, offset, bytesRead, bytes);
        e.onNext(new Prog<FileLink>(bytesSent));

        if (bytesSent < bytesRead) {
          if (bytesSent < Upload.MIN_CHUNK_SIZE) {
            throw new IOException();
          }
          upload.chunkSize = bytesSent;
          // Seek backwards to the byte after where we've successfully sent
          // Otherwise we'd skip bytes when we reduce the chunkSize
          file.seek(((start + i) * upload.partSize) + offset + bytesSent);
        }

        offset += bytesSent;
        bytesLeft -= bytesSent;
      }

      if (upload.intelligent) {
        multipartCommit(upload, part);
      }
    }

    e.onComplete();
  }

  /** Get parameters from Filestack for the upload to S3. */
  private UploadResponse getUploadParams(final Upload upload, int part, int offset,
                                         int size, byte[] bytes)
      throws Exception {

    // Deprecated because MD5 is insecure not because this is unmaintained
    @SuppressWarnings("deprecation")
    HashCode hc = Hashing.md5().newHasher(size).putBytes(bytes, 0, size).hash();
    String md5 = BaseEncoding.base64().encode(hc.asBytes());

    final HashMap<String, RequestBody> params = new HashMap<>();
    params.putAll(upload.baseParams);
    params.put("part", Util.createStringPart(Integer.toString(part)));
    params.put("size", Util.createStringPart(Integer.toString(size)));
    params.put("md5", Util.createStringPart(md5));
    if (upload.intelligent) {
      params.put("offset", Util.createStringPart(Integer.toString(offset)));
    }

    RetryNetworkFunc<UploadResponse> func = new RetryNetworkFunc<UploadResponse>(5, 5, upload.delayBase) {
      @Override
      Response<UploadResponse> work() throws Exception {
        return upload.fsService.upload(params).execute();
      }
    };

    return func.call();
  }

  /** Upload chunk/part to S3. */
  private int uploadToS3(final Upload upload, final int part, final int offset,
                         final int size, final byte[] bytes)
      throws Exception {

    RetryNetworkFunc<Integer> func = new RetryNetworkFunc<Integer>(5, 5, upload.delayBase) {
      private int attemptSize = size;

      @Override
      Response<ResponseBody> work() throws Exception {
        UploadResponse params = getUploadParams(upload, part, offset, attemptSize, bytes);
        Map<String, String> headers = params.getS3Headers();
        String url = params.getUrl();

        RequestBody requestBody = RequestBody.create(upload.mediaType, bytes, 0, attemptSize);
        return upload.fsService.uploadS3(headers, url, requestBody).execute();
      }

      @Override
      public void onNetworkFail(int retries) {
        if (upload.intelligent) {
          attemptSize /= 2;
        }
        super.onNetworkFail(retries);
      }

      @Override
      Integer process(Response response) {
        if (!upload.intelligent) {
          String etag = response.headers().get("ETag");
          upload.etags[part - 1] = etag;
        }
        return attemptSize;
      }
    };

    return func.call();
  }

  /** For intelligent ingestion mode only. Called when all chunks of a part have been uploaded. */
  private void multipartCommit(final Upload upload, int part) throws Exception {
    final HashMap<String, RequestBody> params = new HashMap<>();
    params.putAll(upload.baseParams);
    params.put("part", Util.createStringPart(Integer.toString(part)));

    RetryNetworkFunc<ResponseBody> func = new RetryNetworkFunc<ResponseBody>(5, 5, upload.delayBase) {
      @Override
      Response<ResponseBody> work() throws Exception {
        return upload.fsService.commit(params).execute();
      }
    };

    func.call();
  }
}
