package model.transform;

public class WatermarkTask extends ImageTransformTask {

    WatermarkTask() {
        super("watermark");
    }
    
    public static class Builder {
        private WatermarkTask watermarkTask;
        
        public Builder() {
            this.watermarkTask = new WatermarkTask();
        }

        public Builder file(String handle) {
            watermarkTask.addOption("file", handle);
            return this;
        }

        public Builder size(int size) {
            watermarkTask.addOption("size", size);
            return this;
        }

        public Builder position(String position) {
            watermarkTask.addOption("position", position);
            return this;
        }

        public Builder position(String val1, String val2) {
            watermarkTask.addOption("position", new String[]{val1, val2});
            return this;
        }

        public WatermarkTask build() {
            return watermarkTask;
        }
    }
}
