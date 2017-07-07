package model.transform;

public class PixelateFacesTask extends ImageTransformTask {

    PixelateFacesTask() {
        super("pixelate_faces");
    }
    
    public static class Builder {
        private PixelateFacesTask pixelateFacesTask;
        
        public Builder() {
            this.pixelateFacesTask = new PixelateFacesTask();
        }

        public Builder faces(int face) {
            pixelateFacesTask.addOption("faces", face);
            return this;
        }

        public Builder faces(Integer... faces) {
            pixelateFacesTask.addOption("faces", faces);
            return this;
        }

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

        public Builder amount(int amount) {
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

        public PixelateFacesTask build() {
            return pixelateFacesTask;
        }
    }
}
