package model.transform;

public class OilPaintTask extends ImageTransformTask {

    public OilPaintTask() {
        super("oil_paint");
    }
    
    public static class Builder {
        private OilPaintTask oilPaintTask;
        
        public Builder() {
            this.oilPaintTask = new OilPaintTask();
        }

        public Builder amount(int amount) {
            oilPaintTask.addOption("amount", amount);
            return this;
        }

        public OilPaintTask build() {
            return oilPaintTask;
        }
    }
}
