package com.filestack.internal;

/** Simple internal progress. */
class Prog<T> {
  private int bytes;
  private T data;

  public Prog() {
  }

  public Prog(int bytes) {
    this.bytes = bytes;
  }

  public Prog(T data) {
    this.data = data;
  }

  public int getBytes() {
    return bytes;
  }

  public T getData() {
    return data;
  }
}
