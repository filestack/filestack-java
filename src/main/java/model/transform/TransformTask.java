package model.transform;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generic task object.
 * A "task" in this case is a transformation, for example resize, crop, convert, etc.
 */
public class TransformTask {
    private String name;
    private ArrayList<Option> options;

    TransformTask() {
    }

    TransformTask(String name) {
        this.name = name;
        this.options = new ArrayList<>();
    }

    /**
     * These represent task parameters.
     * For example the resize task would have options for width and height.
     */
    private class Option {
        String key;
        String value;

        Option(String key, String value) {
            this.key = key;
            this.value = value;
        }
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
