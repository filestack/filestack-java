package com.filestack.util;

import com.filestack.FileLink;
import com.filestack.util.responses.CompleteResponse;
import io.reactivex.Flowable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Function to be passed to {@link Flowable#fromCallable(Callable)}.
 * Handles completing a multipart upload, gets metadata for final file.
 * In intelligent ingestion mode the {@link FsUploadService#complete(Map)} call may return a
 * 202 response while the parts are still processing. In this case the {@link RetryNetworkFunc}
 * will handle it like a failure and automatically retry.
 */
public class UploadCompleteFunc implements Callable<Prog<FileLink>> {
  private Upload upload;
  
  UploadCompleteFunc(Upload upload) {
    this.upload = upload;
  }
  
  @Override
  public Prog<FileLink> call() throws Exception {
    final HashMap<String, RequestBody> params = new HashMap<>();
    params.putAll(upload.baseParams);

    if (!upload.intelligent) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < upload.etags.length; i++) {
        builder.append(i + 1).append(':').append(upload.etags[i]).append(';');
      }
      builder.deleteCharAt(builder.length() - 1);
      String parts = builder.toString();
      params.put("parts", Util.createStringPart(parts));
    }

    RetryNetworkFunc<CompleteResponse> func;
    func = new RetryNetworkFunc<CompleteResponse>(5, 5, Upload.DELAY_BASE) {

      @Override
      Response<CompleteResponse> work() throws Exception {
        return upload.fsService.upload().complete(params).execute();
      }
    };

    CompleteResponse response = func.call();
    FileLink fileLink = new FileLink(upload.apiKey, response.getHandle(), upload.security);

    return new Prog<>(fileLink);
  }
}
