package model.transform;

public class CircleTask extends ImageTransformTask {

    CircleTask() {
        super("circle");
    }
    
    public static class Builder {
        private CircleTask circleTask;
        
        public Builder() {
            this.circleTask = new CircleTask();
        }

        public Builder background(String background) {
            circleTask.addOption("background", background);
            return this;
        }

        public CircleTask build() {
            return circleTask;
        }
    }
}
