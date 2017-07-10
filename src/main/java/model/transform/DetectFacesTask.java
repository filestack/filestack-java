package model.transform;

public class DetectFacesTask extends ImageTransformTask {

    public DetectFacesTask() {
        super("detect_faces");
    }
    
    public static class Builder {
        private DetectFacesTask detectFacesTask;
        
        public Builder() {
            this.detectFacesTask = new DetectFacesTask();
        }

        public Builder minSize(int minSize) {
            detectFacesTask.addOption("minsize", minSize);
            return this;
        }

        public Builder minSize(double minSize) {
            detectFacesTask.addOption("minsize", minSize);
            return this;
        }

        public Builder maxSize(int maxSize) {
            detectFacesTask.addOption("maxsize", maxSize);
            return this;
        }

        public Builder maxSize(double maxSize) {
            detectFacesTask.addOption("maxsize", maxSize);
            return this;
        }

        /**
         * This can be a name string like "white" or a hex string like "FFFFFFFF".
         */
        public Builder color(String color) {
            detectFacesTask.addOption("color", color);
            return this;
        }

        public Builder export(boolean export) {
            detectFacesTask.addOption("export", export);
            return this;
        }

        public DetectFacesTask build() {
            return detectFacesTask;
        }
    }
}
