package model.transform;

public class CropFacesTask extends ImageTransformTask {

    CropFacesTask() {
        super("crop_faces");
    }
    
    public static class Builder {
        private CropFacesTask cropFacesTask;
        
        public Builder() {
            this.cropFacesTask = new CropFacesTask();
        }

        public Builder mode(String mode) {
            cropFacesTask.addOption("mode", mode);
            return this;
        }

        public Builder width(int width) {
            cropFacesTask.addOption("width", width);
            return this;
        }

        public Builder height(int height) {
            cropFacesTask.addOption("height", height);
            return this;
        }

        public Builder faces(int face) {
            cropFacesTask.addOption("faces", face);
            return this;
        }

        public Builder faces(Integer... faces) {
            cropFacesTask.addOption("faces", faces);
            return this;
        }

        public Builder faces(String faces) {
            cropFacesTask.addOption("faces", faces);
            return this;
        }

        public Builder buffer(int buffer) {
            cropFacesTask.addOption("buffer", buffer);
            return this;
        }

        public CropFacesTask build() {
            return cropFacesTask;
        }
    }
}
