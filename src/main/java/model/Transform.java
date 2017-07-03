package model;

import okhttp3.HttpUrl;
import util.FilestackService;
import util.Networking;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Base class for file transformations and conversions.
 */
public class Transform {
    String apiKey;
    String source;

    ArrayList<Task> tasks;

    FilestackService.Process processService;

    Transform(Client client, String source) {
        this(client, source, null);
    }

    Transform(FileLink fileLink) {
        this(null, null, fileLink);
    }

    Transform(Client client, String source, FileLink fileLink) {
        if (client != null) {
            this.apiKey = client.getApiKey();
            this.source = source;
        } else {
            this.source = fileLink.getHandle();
        }

        this.tasks = new ArrayList<>();
        this.processService = Networking.getProcessService();

        Security security = client != null ? client.getSecurity() : fileLink.getSecurity();
        if (security != null) {
            Task securityTask = new Task("security");
            securityTask.addOption("policy", security.getPolicy());
            securityTask.addOption("signature", security.getSignature());
            this.tasks.add(securityTask);
        }
    }

    /**
     * Generic task object.
     * A "task" in this case is a transformation, for example resize, crop, convert, etc.
     */
    protected static class Task {
        String name;
        ArrayList<Option> options;

        Task(String name) {
            this.name = name;
            this.options = new ArrayList<>();
        }

        void addOption(String key, Object value) {
            // Passing an empty key is a mistake, shouldn't happen
            if (key == null || key.length() == 0)
                throw new InvalidParameterException("Task option key cannot be empty");
            // Allowing the passing of a null value however, is for convenience
            // If we're leaving out an option for a transform task, we only need to check for that here
            if (value == null)
                return;

            if (value.getClass().isArray()) {
                String valueString = Arrays.toString((Object[])value);
                // Remove spaces between array items
                valueString = valueString.replace(" ", "");
                options.add(new Option(key, valueString));
            } else {
                options.add(new Option(key, value.toString()));
            }
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(name);
            stringBuilder.append("=");
            for (Option option : options)
                if (option.value != null)
                    stringBuilder.append(option.key).append(":").append(option.value).append(",");
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
            return stringBuilder.toString();
        }
    }

    /**
     * Each {@link Task Task} object has options.
     * These are the settings for that task.
     * For example the resize task would have options for width and height.
     */
    protected static class Option {
        String key;
        String value;

        Option(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Build tasks into single string to insert into request.
     */
    protected String getTasksString() {
        if (tasks.size() == 0)
            return "";

        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : tasks)
            stringBuilder.append(task.toString()).append('/');
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    public String url() {
        String tasksString = getTasksString();
        HttpUrl httpUrl;

        if (apiKey != null)
            httpUrl = processService.getExternal(apiKey, tasksString, source).request().url();
        else
            httpUrl = processService.get(tasksString, source).request().url();

        // When forming the request we add a / between tasks, then add that entire string as a path variable
        // Because it's added as a single path variable, the / is URL encoded
        // That's a little confusing so we're replacing "%2F" with "/" for a more expected URL
        return httpUrl.toString().replace("%2F", "/");
    }
}
