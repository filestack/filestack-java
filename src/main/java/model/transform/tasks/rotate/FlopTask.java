package model.transform.tasks.rotate;

import model.transform.base.ImageTransformTask;

public class FlopTask extends ImageTransformTask {

    // Constructor left public because this task can be used with default options
    // Builder doesn't make sense for this task
    public FlopTask() {
        super("flop");
    }
}
