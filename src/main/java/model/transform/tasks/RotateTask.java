package model.transform.tasks;

import model.transform.base.ImageTransformTask;

public class RotateTask extends ImageTransformTask {

    // Constructor made private because this task cannot be used with default options
    private RotateTask() {
        super("rotate");
    }

    public static class Builder {
        private RotateTask rotateTask;
        
        public Builder() {
            this.rotateTask = new RotateTask();
        }

        public Builder deg(int deg) {
            rotateTask.addOption("deg", deg);
            return this;
        }

        // For setting degree to "exif"
        public Builder deg(String deg) {
            rotateTask.addOption("deg", deg);
            return this;
        }

        public Builder exif(boolean exif) {
            rotateTask.addOption("exif", exif);
            return this;
        }

        public Builder background(String background) {
            rotateTask.addOption("background", background);
            return this;
        }

        public RotateTask build() {
            return rotateTask;
        }
    }
}
