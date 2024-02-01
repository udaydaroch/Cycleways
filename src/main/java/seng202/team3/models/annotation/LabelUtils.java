package seng202.team3.models.annotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for working with @Label annotations in classses.
 *
 * @author Hanan Fokkens
 */
public class LabelUtils {
  /**
   * Gets all the @Label attributes.
   *
   * @param clazz The class, eg Object.class
   * @return List of all the labeled attributes
   */
  public static List<String> getLabeledAttributes(Class<?> clazz) {
    List<String> attributes = new ArrayList<>();

    for (Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(Label.class)) {
        attributes.add(field.getName());
      }
    }

    return attributes;
  }

  /**
   * Get the label for a specific field of a class
   *
   * @param clazz The class containing the field
   * @param fieldName The name of the field for which the label is to be retrieved.
   * @return The label value associated with the field, or null if no label is defined.
   * @throws NoSuchFieldException That field doesn't exist
   */
  public static String getLabel(Class<?> clazz, String fieldName) throws NoSuchFieldException {
    Field field = clazz.getDeclaredField(fieldName);

    if (field.isAnnotationPresent(Label.class)) {
      Label label = field.getAnnotation(Label.class);

      return label.value();
    }

    return null;
  }

  /**
   * Gets the field from the label
   *
   * @param clazz The class with the labeled fields
   * @param labelString The label
   * @return The field string
   */
  public static String getField(Class<?> clazz, String labelString) {
    for (Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(Label.class)) {
        Label label = field.getAnnotation(Label.class);

        if (label.value().equals(labelString)) {
          return field.getName();
        }
      }
    }

    return null;
  }
}
