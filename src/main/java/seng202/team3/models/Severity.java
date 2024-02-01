package seng202.team3.models;

/**
 * Severity enum for storing crash severity and easily converting between text and int severity
 * values.
 */
public enum Severity {
  /**
   * Represents an empty or null severity.
   */
  NULL(""),

  /**
   * Represents a non-injury severity.
   */
  NON_INJURY("Non Injury"),

  /**
   * Represents a minor injury severity.
   */
  MINOR_INJURY("Minor Injury"),

  /**
   * Represents a serious injury severity.
   */
  SERIOUS_INJURY("Serious Injury"),

  /**
   * Represents a fatal crash severity.
   */
  FATAL_CRASH("Fatal Crash");

  final String textualSeverity;

  /**
   * Constructor for Severity enum, initializes it with the provided textual severity
   *
   * @param textualSeverity Textual representation of the severity
   */
  Severity(String textualSeverity) {
    this.textualSeverity = textualSeverity;
  }

  /**
   * Convert this Severity object to string
   *
   * @return nicely formatted string of the severity
   */
  @Override
  public String toString() {
    return textualSeverity;
  }

  /**
   * Convert this Severity object to int value
   *
   * @return int value corresponding to this severity
   */
  public int toInt() {
    return this.ordinal();
  }
}
