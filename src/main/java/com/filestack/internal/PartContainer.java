package org.filestack.internal;

class PartContainer {
  byte[] data;
  int num;
  int size;
  int sent;

  PartContainer(int partSize) {
    this.data = new byte[partSize];
  }
}
