package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class FileTypeTask extends ImageTransformTask {

  FileTypeTask() {
    super("output");
  }

  public static class Builder {
    private FileTypeTask fileTypeTask;

    public Builder() {
      this.fileTypeTask = new FileTypeTask();
    }

    public Builder format(String format) {
      fileTypeTask.addOption("format", format);
      return this;
    }

    public Builder background(String background) {
      fileTypeTask.addOption("background", background);
      return this;
    }

    public Builder page(int page) {
      fileTypeTask.addOption("page", page);
      return this;
    }

    public Builder density(int density) {
      fileTypeTask.addOption("density", density);
      return this;
    }

    public Builder compress(boolean compress) {
      fileTypeTask.addOption("compress", compress);
      return this;
    }

    public Builder quality(int quality) {
      fileTypeTask.addOption("quality", quality);
      return this;
    }

    /** For "input" option. */
    public Builder quality(String quality) {
      fileTypeTask.addOption("quality", quality);
      return this;
    }

    public Builder strip(boolean strip) {
      fileTypeTask.addOption("strip", strip);
      return this;
    }

    public Builder colorSpace(String colorSpace) {
      fileTypeTask.addOption("colorspace", colorSpace);
      return this;
    }

    public Builder secure(boolean secure) {
      fileTypeTask.addOption("secure", secure);
      return this;
    }

    public Builder docInfo(boolean docInfo) {
      fileTypeTask.addOption("docinfo", docInfo);
      return this;
    }

    public Builder pageFormat(String pageFormat) {
      fileTypeTask.addOption("pageformat", pageFormat);
      return this;
    }

    public Builder pageOrientation(String pageOrientation) {
      fileTypeTask.addOption("pageorientation", pageOrientation);
      return this;
    }

    public FileTypeTask build() {
      return fileTypeTask;
    }
  }
}
