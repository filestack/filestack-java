package model.transform.tasks;

import model.transform.base.ImageTransformTask;

public class SepiaTask extends ImageTransformTask {

    // Constructor left public because this task can be used with default options
    public SepiaTask() {
        super("sepia");
    }

    // Builder doesn't make sense for this task, there's only 1 option
    public SepiaTask(int tone) {
        super("sepia");
        addOption("tone", tone);
    }
}
