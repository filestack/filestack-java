package model.transform;

public class BlurFacesTask extends ImageTransformTask {

    BlurFacesTask() {
        super("blur_faces");
    }
    
    public static class Builder {
        private BlurFacesTask blurFacesTask;
        
        public Builder() {
            this.blurFacesTask = new BlurFacesTask();
        }

        public Builder faces(int face) {
            blurFacesTask.addOption("faces", face);
            return this;
        }

        public Builder faces(Integer... faces) {
            blurFacesTask.addOption("faces", faces);
            return this;
        }

        public Builder faces(String faces) {
            blurFacesTask.addOption("faces", faces);
            return this;
        }

        public Builder minSize(int minSize) {
            blurFacesTask.addOption("minsize", minSize);
            return this;
        }

        public Builder minSize(double minSize) {
            blurFacesTask.addOption("minsize", minSize);
            return this;
        }

        public Builder maxSize(int maxSize) {
            blurFacesTask.addOption("maxsize", maxSize);
            return this;
        }

        public Builder maxSize(double maxSize) {
            blurFacesTask.addOption("maxsize", maxSize);
            return this;
        }

        public Builder buffer(int buffer) {
            blurFacesTask.addOption("buffer", buffer);
            return this;
        }

        public Builder amount(double amount) {
            blurFacesTask.addOption("amount", amount);
            return this;
        }

        public Builder blur(double blur) {
            blurFacesTask.addOption("blur", blur);
            return this;
        }

        public Builder type(String type) {
            blurFacesTask.addOption("type", type);
            return this;
        }

        public BlurFacesTask build() {
            return blurFacesTask;
        }
    }
}
