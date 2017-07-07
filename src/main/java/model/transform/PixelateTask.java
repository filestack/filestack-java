package model.transform;

public class PixelateTask extends ImageTransformTask {

    public PixelateTask() {
        super("pixelate");
    }
    
    public static class Builder {
        private PixelateTask pixelateTask;
        
        public Builder() {
            this.pixelateTask = new PixelateTask();
        }

        public Builder amount(int amount) {
            pixelateTask.addOption("amount", amount);
            return this;
        }

        public PixelateTask build() {
            return pixelateTask;
        }
    }
}
