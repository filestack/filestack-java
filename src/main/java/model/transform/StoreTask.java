package model.transform;

public class StoreTask extends ImageTransformTask {

    StoreTask() {
        super("store");
    }
    
    public static class Builder {
        private StoreTask storeTask;
        
        public Builder() {
            this.storeTask = new StoreTask();
        }

        public Builder filename(String filename) {
            storeTask.addOption("filename", filename);
            return this;
        }

        public Builder location(String location) {
            storeTask.addOption("location", location);
            return this;
        }

        public Builder path(String path) {
            storeTask.addOption("path", path);
            return this;
        }

        public Builder container(String container) {
            storeTask.addOption("container", container);
            return this;
        }

        public Builder region(String region) {
            storeTask.addOption("region", region);
            return this;
        }

        public Builder access(String access) {
            storeTask.addOption("access", access);
            return this;
        }

        public Builder base64Decode(boolean base64Decode) {
            storeTask.addOption("base64decode", base64Decode);
            return this;
        }

        public StoreTask build() {
            return storeTask;
        }
    }
}
