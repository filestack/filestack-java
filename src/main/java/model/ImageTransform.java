package model;

/**
 * {@link Transform Transform} subclass for image transformations.
 */
public class ImageTransform extends Transform {

    ImageTransform(String apiKey, String source) {
        super(apiKey, source);
    }

    ImageTransform(String handle) {
        super(handle);
    }

    // TODO This is just for demonstration, it should be confirmed when real transforms are added
    public ImageTransform resize(Integer width, Integer height, String fit, String align) {
        Task task = new Task("resize");
        task.addOption("width", width);
        task.addOption("height", height);
        task.addOption("fit", fit);
        task.addOption("align", align);
        tasks.add(task);
        return this;
    }
}
