package model.transform;

public class RotateTask extends ImageTransformTask {

    RotateTask() {
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

        /**
         * This can be a name string like "white" or a hex string like "FFFFFFFF".
         */
        public Builder background(String background) {
            rotateTask.addOption("background", background);
            return this;
        }

        public RotateTask build() {
            return rotateTask;
        }
    }
}
