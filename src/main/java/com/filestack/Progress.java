package com.filestack;

public class Progress<T> {
  private int bytes;
  private T data;

  public Progress(int bytes, T data) {
    this.bytes = bytes;
    this.data = data;
  }

  public int getBytes() {
    return bytes;
  }

  public T getData() {
    return data;
  }
}
