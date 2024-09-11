package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

import java.util.ArrayList;

public class PartialBlurTask extends ImageTransformTask {

  // Constructor made package-private because this task cannot be used with default options
  PartialBlurTask() {
    super("partial_pixelate");
  }

  public static class Builder {
    private PartialBlurTask partialBlurTask;
    private ArrayList<Area> objects;

    public Builder() {
      this.partialBlurTask = new PartialBlurTask();
      objects = new ArrayList<>();
    }

    public Builder amount(double amount) {
      partialBlurTask.addOption("amount", amount);
      return this;
    }

    public Builder blur(double blur) {
      partialBlurTask.addOption("blur", blur);
      return this;
    }

    public Builder type(String type) {
      partialBlurTask.addOption("type", type);
      return this;
    }

    public Builder addArea(int x, int y, int width, int height) {
      objects.add(new Area(x, y, width, height));
      return this;
    }

    public PartialBlurTask build() {
      partialBlurTask.addOption("objects", objects);
      return partialBlurTask;
    }
  }

  private static class Area {
    int originX;
    int originY;
    int width;
    int height;

    public Area(int originX, int originY, int width, int height) {
      this.originX = originX;
      this.originY = originY;
      this.width = width;
      this.height = height;
    }

    @Override
    public String toString() {
      return "[" + originX + "," + originY + "," + width + "," + height + "]";
    }
  }
}
