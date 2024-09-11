package org.filestack.transforms.tasks;

import org.filestack.transforms.ImageTransformTask;

public class TornEdgesTask extends ImageTransformTask {

  // Constructor left public because this task can be used with default options
  public TornEdgesTask() {
    super("torn_edges");
  }

  public static class Builder {
    private TornEdgesTask tornEdgesTask;

    public Builder() {
      this.tornEdgesTask = new TornEdgesTask();
    }

    public Builder spread(int first, int second) {
      tornEdgesTask.addOption("spread", new Integer[] {first, second});
      return this;
    }

    public Builder background(String background) {
      tornEdgesTask.addOption("background", background);
      return this;
    }

    public TornEdgesTask build() {
      return tornEdgesTask;
    }
  }
}
