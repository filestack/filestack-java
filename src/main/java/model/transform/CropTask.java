package model.transform;

public class CropTask extends ImageTransformTask {

    CropTask(int x, int y, int width, int height) {
        super("crop");
        this.addOption("dim", new Integer[]{x, y, width, height});
    }
}
