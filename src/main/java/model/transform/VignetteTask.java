package model.transform;

public class VignetteTask extends ImageTransformTask {

    VignetteTask() {
        super("vignette");
    }
    
    public static class Builder {
        private VignetteTask vignetteTask;
        
        public Builder() {
            this.vignetteTask = new VignetteTask();
        }

        public Builder amount(int amount) {
            vignetteTask.addOption("amount", amount);
            return this;
        }

        public Builder blurMode(String blurMode) {
            vignetteTask.addOption("blurmode", blurMode);
            return this;
        }

        public Builder background(String background) {
            vignetteTask.addOption("background", background);
            return this;
        }

        public VignetteTask build() {
            return vignetteTask;
        }
    }
}
