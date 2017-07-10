package model.transform;

public class SharpenTask extends ImageTransformTask {

    public SharpenTask() {
        super("sharpen");
    }
    
    public static class Builder {
        private SharpenTask sharpenTask;
        
        public Builder() {
            this.sharpenTask = new SharpenTask();
        }

        public Builder amount(int amount) {
            sharpenTask.addOption("amount", amount);
            return this;
        }

        public SharpenTask build() {
            return sharpenTask;
        }
    }
}
