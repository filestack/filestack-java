package com.filestack.internal;

import com.filestack.FileLink;
import com.filestack.Progress;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.util.List;
import org.reactivestreams.Publisher;

public class ProgMapFunc implements Function<List<Prog<FileLink>>, Publisher<Progress<FileLink>>> {
  private final Upload upload;
  private final long startTime = System.currentTimeMillis();

  private double avgRate; // bytes / second
  private long bytesSent;

  ProgMapFunc(Upload upload) {
    this.upload = upload;
  }

  @Override
  public Publisher<Progress<FileLink>> apply(List<Prog<FileLink>> progs) throws Exception {
    // Skip update if buffer is empty
    if (progs.size() == 0) {
      return Flowable.empty();
    }

    int bytes = 0;
    FileLink data = null;
    for (Prog<FileLink> simple : progs) {
      bytes += simple.getBytes();
      data = simple.getData();
    }

    // Bytes could equal 0 if we only have a status from start or complete func
    // We don't want to update the rate for requests that don't carry file content
    if (bytes != 0) {
      bytesSent += bytes;
      if (avgRate == 0) {
        avgRate = bytes;
      } else {
        avgRate = Progress.calcAvg(bytes, avgRate);
      }
    }

    // Skip update if we haven't sent anything or are waiting on the complete func
    if (bytesSent == 0 || (bytesSent / upload.filesize == 1 && data == null)) {
      return Flowable.empty();
    }

    long currentTime = System.currentTimeMillis();
    int elapsed = (int) ((currentTime - startTime) / 1000L);

    double rate = avgRate / Upload.PROG_INTERVAL; // Want bytes / second not bytes / interval
    return Flowable.just(new Progress<>(bytesSent, upload.filesize, elapsed, rate, data));
  }
}
