package org.filestack.internal;

import org.filestack.FileLink;

/** Simple internal events for the upload process. */
class Prog {

  enum Type {
    START,
    TRANSFER,
    COMPLETE
  }

  private Type type;
  private long startTime; // Unix time in seconds
  private long endTime;
  private int bytes;
  private FileLink fileLink;

  // For the start function
  public Prog(long startTime, long endTime) {
    this.type = Type.START;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  // For the transfer function
  public Prog(long startTime, long endTime, int bytes) {
    this.type = Type.TRANSFER;
    this.startTime = startTime;
    this.endTime = endTime;
    this.bytes = bytes;
  }

  // For the complete function
  public Prog(long startTime, long endTime, FileLink fileLink) {
    this.type = Type.COMPLETE;
    this.startTime = startTime;
    this.endTime = endTime;
    this.fileLink = fileLink;
  }

  double getRate() {
    long diff = endTime - startTime;
    return bytes / (diff > 0 ? diff : 1);
  }

  double getElapsed() {
    return endTime - startTime;
  }

  Type getType() {
    return type;
  }

  long getStartTime() {
    return startTime;
  }

  long getEndTime() {
    return endTime;
  }

  int getBytes() {
    return bytes;
  }

  FileLink getFileLink() {
    return fileLink;
  }
}
