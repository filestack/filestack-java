package com.filestack;

public class Progress<T> {
  private static final double SMOOTHING_FACTOR = 0.005;

  private final long bytesMoved;
  private final long bytesTotal;
  private final int elapsed;
  private final double rate;
  private final T data;

  public Progress(long bytesMoved, long bytesTotal, int elapsed, double rate, T data) {
    this.bytesMoved = bytesMoved;
    this.bytesTotal = bytesTotal;
    this.elapsed = elapsed;
    this.rate = rate;
    this.data = data;
  }

  /** Calculates exponential moving average. */
  public static double calcAvg(double lastRate, double avgRate) {
    return SMOOTHING_FACTOR * lastRate + (1 - SMOOTHING_FACTOR) * avgRate;
  }

  public long getBytesMoved() {
    return bytesMoved;
  }

  public long getBytesTotal() {
    return bytesTotal;
  }

  public double getPercent() {
    return bytesMoved / (double) bytesTotal;
  }

  /** Time elapsed in seconds. */
  public int getElapsed() {
    return elapsed;
  }

  /** Average transfer rate in bytes/second. */
  public double getRate() {
    return rate;
  }

  /** Estimated time left in seconds given current progress and rate. */
  public int getEta() {
    return (int) Math.ceil((bytesTotal - bytesMoved) / rate);
  }

  /** Get any data associated with the transfer. May be null. */
  public T getData() {
    return data;
  }
}
