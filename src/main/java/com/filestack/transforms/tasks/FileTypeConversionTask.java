package com.filestack.transforms.tasks;

import com.filestack.transforms.ImageTransformTask;

public class FileTypeConversionTask extends ImageTransformTask {

  FileTypeConversionTask() {
    super("output");
  }

  public static class Builder {
    private FileTypeConversionTask fileTypeConversionTask;

    public Builder() {
      this.fileTypeConversionTask = new FileTypeConversionTask();
    }

    public Builder format(String format) {
      fileTypeConversionTask.addOption("format", format);
      return this;
    }

    public Builder background(String background) {
      fileTypeConversionTask.addOption("background", background);
      return this;
    }

    public Builder page(int page) {
      fileTypeConversionTask.addOption("page", page);
      return this;
    }

    public Builder density(int density) {
      fileTypeConversionTask.addOption("density", density);
      return this;
    }

    public Builder compress(boolean compress) {
      fileTypeConversionTask.addOption("compress", compress);
      return this;
    }

    public Builder quality(int quality) {
      fileTypeConversionTask.addOption("quality", quality);
      return this;
    }

    /** For "input" option. */
    public Builder quality(String quality) {
      fileTypeConversionTask.addOption("quality", quality);
      return this;
    }

    public Builder strip(boolean strip) {
      fileTypeConversionTask.addOption("strip", strip);
      return this;
    }

    public Builder colorSpace(String colorSpace) {
      fileTypeConversionTask.addOption("colorspace", colorSpace);
      return this;
    }

    public Builder secure(boolean secure) {
      fileTypeConversionTask.addOption("secure", secure);
      return this;
    }

    public Builder docInfo(boolean docInfo) {
      fileTypeConversionTask.addOption("docinfo", docInfo);
      return this;
    }

    public Builder pageFormat(String pageFormat) {
      fileTypeConversionTask.addOption("pageformat", pageFormat);
      return this;
    }

    public Builder pageOrientation(String pageOrientation) {
      fileTypeConversionTask.addOption("pageorientation", pageOrientation);
      return this;
    }

    public FileTypeConversionTask build() {
      return fileTypeConversionTask;
    }
  }
}
