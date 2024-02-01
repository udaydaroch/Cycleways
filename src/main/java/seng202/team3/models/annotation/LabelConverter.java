package seng202.team3.models.annotation;

import javafx.util.StringConverter;

/**
 * Converter that converse a class attribute from a labeled class into a nice formatted one.
 *
 * @author Hanan Fokkens
 */
public class LabelConverter extends StringConverter<String> {
  private Class<?> clazz;

  /**
   * Initializes a new LabelConverter with the specified class. This converter can be used to
   * retrieve user-friendly labels for the fields of the given class.
   *
   * @param clazz The class for which the converter will operate.
   */
  public LabelConverter(Class<?> clazz) {
    this.clazz = clazz;
  }

  /**
   * Converts a field name to its user-friendly label representation using the LabelUtils utility.
   *
   * @param field The field name to be converted.
   * @return The user-friendly label for the given field. Returns null if the field does not exist
   *     in the specified class.
   */
  @Override
  public String toString(String field) {
    try {
      return LabelUtils.getLabel(clazz, field);
    } catch (NoSuchFieldException e) {
      return null;
    }
  }

  /**
   * Converts a user-friendly label back to its corresponding field name for the specified class.
   *
   * @param label The user-friendly label to be converted back to its field name.
   * @return The field name corresponding to the given label.
   */
  @Override
  public String fromString(String label) {
    return LabelUtils.getField(clazz, label);
  }
}
