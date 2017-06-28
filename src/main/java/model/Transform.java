package model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Base class for file transformations and conversions.
 */
public class Transform {
    private Client client;
    private String source;
    private FileLink fileLink;

    ArrayList<Task> tasks;

    Transform(Client client, String source) {
        this.client = client;
        this.source = source;
        this.tasks = new ArrayList<>();
    }

    Transform(FileLink fileLink) {
        this.fileLink = fileLink;
        this.tasks = new ArrayList<>();
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
            addOption(key, value.toString());
        }

        void addOption(String key, Object value[]) {
            String valueString = Arrays.toString(value);
            // Remove spaces between array items
            valueString = valueString.replace(" ", "");
            addOption(key, valueString);
        }

        void addOption(String key, String value) {
            options.add(new Option(key, value));
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(name);
            stringBuilder.append("=");
            for (Option option : options)
                stringBuilder.append(option.key).append(":").append(option.value).append(",");
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
}
