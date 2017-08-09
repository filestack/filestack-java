package com.filestack.model.transform.base;

import com.filestack.model.FileLink;
import com.filestack.model.FilestackClient;
import com.filestack.model.transform.tasks.StoreOptions;
import com.filestack.util.FilestackService;
import com.google.gson.JsonObject;
import java.io.IOException;

/**
 * {@link Transform Transform} subclass for image transformations.
 */
public class ImageTransform extends Transform {

    public ImageTransform(FilestackClient fsClient, String source) {
        super(fsClient, source);
    }

    public ImageTransform(FileLink fileLink) {
        super(fileLink);
    }

    /**
     * Debugs the transformation as built so far, returning explanations of any issues.
     *
     * @return {@link JsonObject JSON} report for transformation
     * @throws IOException if a network error occurs, handle doesn't exist, or security is invalid
     */
    public JsonObject debug() throws IOException {
        String tasksString = getTasksString();

        if (apiKey != null) {
            return processService.debugExternal(apiKey, tasksString, source).execute().body();
        } else {
            return processService.debug(tasksString, source).execute().body();
        }
    }

    public FileLink store() throws IOException {
        return store(null);
    }

    /**
     * Stores the result of a transformation into a new file.
     * https://www.filestack.com/docs/image-transformations/store
     *
     * @param storeOptions configure where and how your file is stored
     * @return new {@link FileLink FileLink} pointing to the file
     * @throws IOException if a network error occurs, handle doesn't exist, or security is invalid
     */
    public FileLink store(StoreOptions storeOptions) throws IOException {
        if (storeOptions == null) {
            storeOptions = new StoreOptions();
        }
        addTask(storeOptions);

        FilestackService.Process.StoreResponse response;
        String tasksString = getTasksString();

        if (apiKey != null) {
            response = processService.storeExternal(apiKey, tasksString, source).execute().body();
        } else {
            response = processService.store(tasksString, source).execute().body();
        }

        String handle = response.getUrl().split("/")[3];
        return new FileLink(apiKey, handle, security);
    }

    /**
     * Add a new transformation to the chain.
     * Tasks are executed in the order they are added.
     *
     * @param task any one of the available {@link ImageTransformTask ImageTransformTask} subclasses
     */
    public ImageTransform addTask(ImageTransformTask task) {
        if (task == null) {
            throw new IllegalArgumentException("Cannot add null task to image transform");
        }
        tasks.add(task);
        return this;
    }
}
