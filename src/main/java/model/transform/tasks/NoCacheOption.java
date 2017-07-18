package model.transform.tasks;

import model.transform.base.ImageTransformTask;

public class NoCacheOption extends ImageTransformTask {

    // Builder doesn't make sense for this task
    public NoCacheOption() {
        super("cache=false");
    }
}
