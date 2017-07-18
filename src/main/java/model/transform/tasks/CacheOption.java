package model.transform.tasks;

import model.transform.base.ImageTransformTask;

public class CacheOption extends ImageTransformTask {

    // Builder doesn't make sense for this task
    public CacheOption(int expiry) {
        super("cache");
        addOption("expiry", expiry);
    }
}
