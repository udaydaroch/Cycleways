package seng202.team3.unittests.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import seng202.team3.models.Severity;

public class SeverityEnumTest {
  Severity severityNull = Severity.NULL;
  Severity severityNonInjury = Severity.NON_INJURY;
  Severity severityMinorInjury = Severity.MINOR_INJURY;
  Severity severitySeriousInjury = Severity.SERIOUS_INJURY;

  Severity severityFatalCrash = Severity.FATAL_CRASH;

  @Test
  void severityToString() {
    assertEquals("", severityNull.toString());
    assertEquals("Non Injury", severityNonInjury.toString());
    assertEquals("Minor Injury", severityMinorInjury.toString());
    assertEquals("Serious Injury", severitySeriousInjury.toString());
    assertEquals("Fatal Crash", severityFatalCrash.toString());
  }

  @Test
  void severityToInt() {
    assertEquals(0, severityNull.toInt());
    assertEquals(1, severityNonInjury.toInt());
    assertEquals(2, severityMinorInjury.toInt());
    assertEquals(3, severitySeriousInjury.toInt());
    assertEquals(4, severityFatalCrash.toInt());
  }
}
