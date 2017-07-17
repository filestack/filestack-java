package model.transform.tasks;

import model.transform.base.ImageTransformTask;

import java.util.ArrayList;

public class PartialPixelateTask extends ImageTransformTask {

    // Constructor made private because this task cannot be used with default options
    private PartialPixelateTask() {
        super("partial_pixelate");
    }

    public static class Builder {
        private PartialPixelateTask partialPixelateTask;
        private ArrayList<Area> objects;
        
        public Builder() {
            this.partialPixelateTask = new PartialPixelateTask();
            objects = new ArrayList<>();
        }

        public Builder amount(int amount) {
            partialPixelateTask.addOption("amount", amount);
            return this;
        }

        public Builder blur(double blur) {
            partialPixelateTask.addOption("blur", blur);
            return this;
        }

        public Builder type(String type) {
            partialPixelateTask.addOption("type", type);
            return this;
        }

        public Builder addArea(int x, int y, int width, int height) {
            objects.add(new Area(x, y, width, height));
            return this;
        }

        public PartialPixelateTask build() {
            partialPixelateTask.addOption("objects", objects);
            return partialPixelateTask;
        }
    }

    private static class Area {
        int x;
        int y;
        int width;
        int height;

        public Area(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "[" + x + "," + y + "," + width + "," + height + "]";
        }
    }
}
