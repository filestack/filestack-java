package model.transform;

public class CropTask extends ImageTransformTask {

    CropTask() {
        super("crop");
    }

    public static class Builder {
        private CropTask cropTask;
        private int x;
        private int y;
        private int width;
        private int height;

        public Builder() {
            this.cropTask = new CropTask();
        }

        public Builder x(int x) {
            this.x = x;
            return this;
        }

        public Builder y(int y) {
            this.y = y;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public CropTask build() {
            cropTask.addOption("dim", new Integer[]{x, y, width, height});
            return cropTask;
        }
    }
}
