package model.transform.tasks.enhancements;

import model.transform.base.ImageTransformTask;

public class EnhanceTask extends ImageTransformTask {

    // Constructor left public because this task can be used with default options
    // Builder doesn't make sense for this task
    public EnhanceTask() {
        super("enhance");
    }
}
