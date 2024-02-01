package seng202.team3.exceptions;

/**
 * Custom DuplicateEntryException to be thrown if a db record creation is attempted with a duplicate
 * key
 *
 * @author Morgan English
 */
public class DuplicateEntryException extends Exception {

  /**
   * Simple constructor that passes to parent Exception class
   *
   * @param message error message
   */
  public DuplicateEntryException(String message) {
    super(message);
  }
}
