package model.transform;

public class BlackWhiteTask extends ImageTransformTask {

    public BlackWhiteTask() {
        super("blackwhite");
    }
    
    public static class Builder {
        private BlackWhiteTask blackWhiteTask;
        
        public Builder() {
            this.blackWhiteTask = new BlackWhiteTask();
        }

        public Builder threshold(int threshold) {
            blackWhiteTask.addOption("threshold", threshold);
            return this;
        }

        public BlackWhiteTask build() {
            return blackWhiteTask;
        }
    }
}
