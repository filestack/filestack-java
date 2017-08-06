package com.filestack.model.transform.tasks;

import com.filestack.model.transform.base.ImageTransformTask;

import java.util.ArrayList;

public class CollageTask extends ImageTransformTask {

    // Constructor made private because this task cannot be used with default options
    private CollageTask() {
        super("collage");
    }

    public static class Builder {
        private CollageTask collageTask;
        private ArrayList<String> files;

        public Builder() {
            this.collageTask = new CollageTask();
            files = new ArrayList<>();
        }

        public Builder addFile(String handle) {
            files.add(handle);
            return this;
        }

        public Builder margin(int margin) {
            collageTask.addOption("margin", margin);
            return this;
        }

        public Builder width(int width) {
            collageTask.addOption("width", width);
            return this;
        }

        public Builder height(int height) {
            collageTask.addOption("height", height);
            return this;
        }

        public Builder color(String color) {
            collageTask.addOption("color", color);
            return this;
        }

        public Builder fit(String fit) {
            collageTask.addOption("fit", fit);
            return this;
        }

        public Builder autoRotate(boolean autoRotate) {
            collageTask.addOption("autorotate", autoRotate);
            return this;
        }

        public CollageTask build() {
            collageTask.addOption("files", files);
            return collageTask;
        }
    }
}
