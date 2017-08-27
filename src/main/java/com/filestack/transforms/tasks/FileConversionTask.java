package com.filestack.transforms.tasks;

import com.filestack.transforms.ImageTransformTask;

public class FileConversionTask extends ImageTransformTask {

  FileConversionTask() {
    super("output");
  }

  public static class Builder {
    private FileConversionTask fileConversionTask;

    public Builder() {
      this.fileConversionTask = new FileConversionTask();
    }

    public Builder format(String format) {
      fileConversionTask.addOption("format", format);
      return this;
    }

    public Builder background(String background) {
      fileConversionTask.addOption("background", background);
      return this;
    }

    public Builder page(int page) {
      fileConversionTask.addOption("page", page);
      return this;
    }

    public Builder density(int density) {
      fileConversionTask.addOption("density", density);
      return this;
    }

    public Builder compress(boolean compress) {
      fileConversionTask.addOption("compress", compress);
      return this;
    }

    public Builder quality(int quality) {
      fileConversionTask.addOption("quality", quality);
      return this;
    }

    /** For "input" option. */
    public Builder quality(String quality) {
      fileConversionTask.addOption("quality", quality);
      return this;
    }

    public Builder strip(boolean strip) {
      fileConversionTask.addOption("strip", strip);
      return this;
    }

    public Builder colorSpace(String colorSpace) {
      fileConversionTask.addOption("colorspace", colorSpace);
      return this;
    }

    public Builder secure(boolean secure) {
      fileConversionTask.addOption("secure", secure);
      return this;
    }

    public Builder docInfo(boolean docInfo) {
      fileConversionTask.addOption("docinfo", docInfo);
      return this;
    }

    public Builder pageFormat(String pageFormat) {
      fileConversionTask.addOption("pageformat", pageFormat);
      return this;
    }

    public Builder pageOrientation(String pageOrientation) {
      fileConversionTask.addOption("pageorientation", pageOrientation);
      return this;
    }

    public FileConversionTask build() {
      return fileConversionTask;
    }
  }
}
