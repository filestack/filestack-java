package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class AsciiTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public AsciiTask() {
    super("ascii");
  }

  public static class Builder {
    private AsciiTask asciiTask;

    public Builder() {
      this.asciiTask = new AsciiTask();
    }

    public Builder background(String background) {
      asciiTask.addOption("background", background);
      return this;
    }

    public Builder foreground(String foreground) {
      asciiTask.addOption("foreground", foreground);
      return this;
    }

    public Builder colored(boolean colored) {
      asciiTask.addOption("colored", colored);
      return this;
    }

    public Builder size(int size) {
      asciiTask.addOption("size", size);
      return this;
    }

    public Builder reverse(boolean reverse) {
      asciiTask.addOption("reverse", reverse);
      return this;
    }

    public AsciiTask build() {
      return asciiTask;
    }
  }
}
