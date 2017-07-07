package model.transform;

public class TornEdgesTask extends ImageTransformTask {

    TornEdgesTask() {
        super("torn_edges");
    }
    
    public static class Builder {
        private TornEdgesTask polaroidTask;
        
        public Builder() {
            this.polaroidTask = new TornEdgesTask();
        }

        public Builder spread(int val1, int val2) {
            polaroidTask.addOption("spread", new Integer[]{val1, val2});
            return this;
        }

        public Builder background(String background) {
            polaroidTask.addOption("background", background);
            return this;
        }

        public TornEdgesTask build() {
            return polaroidTask;
        }
    }
}
