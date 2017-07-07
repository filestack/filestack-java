package model.transform;

public class BlurTask extends ImageTransformTask {

    public BlurTask() {
        super("blur");
    }
    
    public static class Builder {
        private BlurTask blurTask;
        
        public Builder() {
            this.blurTask = new BlurTask();
        }

        public Builder amount(int amount) {
            blurTask.addOption("amount", amount);
            return this;
        }

        public BlurTask build() {
            return blurTask;
        }
    }
}
