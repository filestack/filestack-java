package model.transform;

public class StoreTask extends ImageTransformTask {

    public StoreTask() {
        super("store");
    }

    public StoreTask filename(String filename) {
        addOption("filename", filename);
        return this;
    }

    public StoreTask location(String location) {
        addOption("location", location);
        return this;
    }

    public StoreTask path(String path) {
        addOption("path", path);
        return this;
    }

    public StoreTask container(String container) {
        addOption("container", container);
        return this;
    }

    public StoreTask region(String region) {
        addOption("region", region);
        return this;
    }

    public StoreTask access(String access) {
        addOption("access", access);
        return this;
    }

    public StoreTask base64Decode(boolean base64Decode) {
        addOption("base64decode", base64Decode);
        return this;
    }
}
