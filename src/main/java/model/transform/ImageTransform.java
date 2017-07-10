package model.transform;

import com.google.gson.JsonObject;
import model.Client;
import model.FileLink;
import util.FilestackService;

import java.io.IOException;

/**
 * {@link Transform Transform} subclass for image transformations.
 */
public class ImageTransform extends Transform {

    public ImageTransform(Client client, String source) {
        super(client, source);
    }

    public ImageTransform(FileLink fileLink) {
        super(fileLink);
    }

    public JsonObject debug() throws IOException {
        String tasksString = getTasksString();

        if (apiKey != null)
            return processService.debugExternal(apiKey, tasksString, source).execute().body();
        else
            return processService.debug(tasksString, source).execute().body();
    }

    public FileLink store() throws IOException {
        return store(null);
    }

    public FileLink store(StoreOptions storeOptions) throws IOException {
        if (storeOptions == null)
            storeOptions = new StoreOptions();
        addTask(storeOptions);

        FilestackService.Process.StoreResponse response;
        String tasksString = getTasksString();

        if (apiKey != null)
            response = processService.storeExternal(apiKey, tasksString, source).execute().body();
        else
            response = processService.store(tasksString, source).execute().body();

        String handle = response.getUrl().split("/")[3];
        return new FileLink(apiKey, handle, security);
    }

    public ImageTransform addTask(ImageTransformTask task) {
        if (task == null)
            throw new IllegalArgumentException("Cannot add null task to image transform");
        tasks.add(task);
        return this;
    }
}
