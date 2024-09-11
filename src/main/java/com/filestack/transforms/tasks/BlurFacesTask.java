package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class BlurFacesTask extends ImageTransformTask {

  // Constructor made package-private because this task cannot be used with default options
  BlurFacesTask() {
    super("pixelate_faces");
  }

  public static class Builder {
    private BlurFacesTask pixelateFacesTask;

    public Builder() {
      this.pixelateFacesTask = new BlurFacesTask();
    }

    /**
     * Sets which faces to blur.
     * Face numbers correspond to the order in which they appear in an image.
     *
     * @param faces one or more integers (faces) to blur
     */
    public Builder faces(int... faces) {
      if (faces.length == 1) {
        pixelateFacesTask.addOption("faces", faces[0]);
        return this;
      }

      Integer[] objectArray = new Integer[faces.length];
      for (int i = 0; i < faces.length; i++) {
        objectArray[i] = faces[i];
      }
      pixelateFacesTask.addOption("faces", objectArray);
      return this;
    }

    /**
     * Allows setting "all" for face selection.
     */
    public Builder faces(String faces) {
      pixelateFacesTask.addOption("faces", faces);
      return this;
    }

    public Builder minSize(int minSize) {
      pixelateFacesTask.addOption("minsize", minSize);
      return this;
    }

    public Builder minSize(double minSize) {
      pixelateFacesTask.addOption("minsize", minSize);
      return this;
    }

    public Builder maxSize(int maxSize) {
      pixelateFacesTask.addOption("maxsize", maxSize);
      return this;
    }

    public Builder maxSize(double maxSize) {
      pixelateFacesTask.addOption("maxsize", maxSize);
      return this;
    }

    public Builder buffer(int buffer) {
      pixelateFacesTask.addOption("buffer", buffer);
      return this;
    }

    public Builder amount(double amount) {
      pixelateFacesTask.addOption("amount", amount);
      return this;
    }

    public Builder blur(double blur) {
      pixelateFacesTask.addOption("blur", blur);
      return this;
    }

    public Builder type(String type) {
      pixelateFacesTask.addOption("type", type);
      return this;
    }

    public BlurFacesTask build() {
      return pixelateFacesTask;
    }
  }
}
