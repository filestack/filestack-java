package org.filestack.transforms;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Generic transform task object.
 * A "task" in this case is a transformation, for example resize, crop, convert, etc.
 */
public class TransformTask {
  String name;
  ArrayList<Option> options;

  public TransformTask(String name) {
    this.name = name;
    this.options = new ArrayList<>();
  }

  /**
   * These represent task parameters.
   * For example the resize task would have options for width and height.
   */
  class Option {
    String key;
    String value;

    Option(String key, String value) {
      this.key = key;
      this.value = value;
    }
  }

  /**
   * Add a key-value option pair to this task.
   */
  public void addOption(String key, @Nullable Object value) {
    // Passing an empty key is a mistake, shouldn't happen
    if (key == null || key.length() == 0) {
      throw new IllegalArgumentException("Task option key cannot be empty");
    }
    // Allowing the passing of a null value however, is for convenience
    // If we're leaving out an option for a transform task, we only need to check for that here
    if (value == null) {
      return;
    }

    String valueString;

    // If value is a an array (not a Collection)
    if (value.getClass().isArray()) {
      valueString = Arrays.toString((Object[]) value);
    } else {
      valueString = value.toString();
    }

    // Remove spaces between array or Collection items
    if (value.getClass().isArray() || value instanceof Collection) {
      valueString = valueString.replace(", ", ",");
    }

    options.add(new Option(key, valueString));
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(name);
    stringBuilder.append("=");
    for (Option option : options) {
      if (option.value != null) {
        stringBuilder.append(option.key).append(":").append(option.value).append(",");
      }
    }
    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    return stringBuilder.toString();
  }

  /**
   * Merges options from multiple tasks and returns a new task with combined options.
   * Does not account for duplicate options.
   */
  public static TransformTask merge(String newName, TransformTask... tasks) {
    ArrayList<Option> options = new ArrayList<>();
    for (TransformTask task : tasks) {
      options.addAll(task.options);
    }
    TransformTask newTask = new TransformTask(newName);
    newTask.options = options;
    return newTask;
  }
}
