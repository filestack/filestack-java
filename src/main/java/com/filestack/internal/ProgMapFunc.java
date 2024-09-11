package org.filestack.internal;

import org.filestack.FileLink;
import org.filestack.Progress;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import org.reactivestreams.Publisher;

public class ProgMapFunc implements Function<Prog, Publisher<Progress<FileLink>>> {

  private static final double SMOOTHING_FACTOR = 0.25; // How quickly we discard old observations

  private final Upload upload; // Just needed to know the total size of the upload
  private long startTime; // Time we received start event
  private long transBytes; // Number of bytes transferred
  private double movAvgRate; // Rate that increasingly devalues older rates (bytes / second)

  ProgMapFunc(Upload upload) {
    this.upload = upload;
  }

  @Override
  public Publisher<Progress<FileLink>> apply(Prog prog) throws Exception {

    if (prog.getType() == Prog.Type.TRANSFER) {

      // Use the first update's rate as the initial value for moving average
      if (transBytes == 0) {
        movAvgRate = prog.getRate();
      }

      transBytes += prog.getBytes();
      movAvgRate = calcMovAvg(prog.getRate(), movAvgRate);

      // Send a progress update
      return createUpdate(prog);
    }

    // Don't send an update when we don't have a rate
    if (prog.getType() == Prog.Type.START) {
      startTime = System.currentTimeMillis() / 1000;
      return Flowable.empty();
    }

    return createUpdate(prog);
  }

  // Calculates exponential moving average.
  private double calcMovAvg(double current, double average) {
    return SMOOTHING_FACTOR * current + (1 - SMOOTHING_FACTOR) * average;
  }

  // Creates a progress update from the current state
  private Flowable<Progress<FileLink>> createUpdate(Prog prog) {
    long currentTime = System.currentTimeMillis() / 1000;
    int elapsedTime = (int) (currentTime - startTime);
    return Flowable.just(new Progress<>(transBytes, upload.inputSize, elapsedTime, movAvgRate * Upload.CONCURRENCY,
        prog.getFileLink()));
  }

}
