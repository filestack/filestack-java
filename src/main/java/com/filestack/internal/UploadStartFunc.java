package com.filestack.internal;

import com.filestack.FileLink;
import com.filestack.internal.responses.StartResponse;
import io.reactivex.Flowable;
import retrofit2.Response;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Function to be passed to {@link Flowable#fromCallable(Callable)}.
 * Handles initiating a multipart upload.
 */
public class UploadStartFunc implements Callable<Prog<FileLink>> {
  private final Upload upload;
  
  UploadStartFunc(Upload upload) {
    this.upload = upload;
  }

  @Override
  public Prog<FileLink> call() throws Exception {
    // Open the file here so that any exceptions with it get passed through the observable
    // Otherwise we'd have an async method that directly throws exceptions
    File file = Util.createReadFile(upload.path);
    upload.filesize = file.length();

    if (!upload.baseParams.containsKey("filename")) {
      upload.baseParams.put("filename", Util.createStringPart(file.getName()));
    }
    upload.baseParams.put("size", Util.createStringPart(Long.toString(upload.filesize)));

    RetryNetworkFunc<StartResponse> func;
    func = new RetryNetworkFunc<StartResponse>(0, 5, Upload.DELAY_BASE) {
      @Override
      Response<StartResponse> work() throws Exception {
        return Networking.getUploadService()
            .start(upload.baseParams)
            .execute();
      }
    };

    StartResponse response = func.call();

    upload.baseParams.putAll(response.getUploadParams());
    upload.intelligent = response.isIntelligent();
    if (upload.intelligent) {
      upload.partSize = 8 * 1024 * 1024;
    } else {
      upload.baseParams.remove("multipart");
      upload.partSize = 5 * 1024 * 1024;
    }

    upload.numParts = (int) Math.ceil(upload.filesize / (double) upload.partSize);
    upload.partsPerFunc = (int) Math.ceil(upload.numParts / (double) Upload.CONCURRENCY);

    upload.etags = new String[upload.numParts];

    return new Prog<>();
  }
}
