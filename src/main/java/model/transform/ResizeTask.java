package model.transform;

public class ResizeTask extends ImageTransformTask {

    ResizeTask() {
        super("resize");
    }
    
    public static class Builder {
        private ResizeTask resizeTask;
        
        public Builder() {
            this.resizeTask = new ResizeTask();
        }

        public Builder width(int width) {
            resizeTask.addOption("width", width);
            return this;
        }

        public Builder height(int height) {
            resizeTask.addOption("height", height);
            return this;
        }

        public Builder fit(String fit) {
            resizeTask.addOption("fit", fit);
            return this;
        }

        public Builder align(String align) {
            resizeTask.addOption("align", align);
            return this;
        }

        public Builder align(String align1, String align2) {
            resizeTask.addOption("align", new String[]{align1, align2});
            return this;
        }

        public ResizeTask build() {
            return resizeTask;
        }
    }
}
