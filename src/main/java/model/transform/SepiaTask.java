package model.transform;

public class SepiaTask extends ImageTransformTask {

    public SepiaTask() {
        super("sepia");
    }
    
    public static class Builder {
        private SepiaTask sepiaTask;
        
        public Builder() {
            this.sepiaTask = new SepiaTask();
        }

        public Builder tone(int tone) {
            sepiaTask.addOption("tone", tone);
            return this;
        }

        public SepiaTask build() {
            return sepiaTask;
        }
    }
}
