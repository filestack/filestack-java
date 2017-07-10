package model.transform;

public class ModulateTask extends ImageTransformTask {

    public ModulateTask() {
        super("modulate");
    }
    
    public static class Builder {
        private ModulateTask modulateTask;
        
        public Builder() {
            this.modulateTask = new ModulateTask();
        }

        public Builder brightness(int brightness) {
            modulateTask.addOption("brightness", brightness);
            return this;
        }

        public Builder hue(int hue) {
            modulateTask.addOption("hue", hue);
            return this;
        }

        public Builder saturation(int saturation) {
            modulateTask.addOption("saturation", saturation);
            return this;
        }

        public ModulateTask build() {
            return modulateTask;
        }
    }
}
