package seng202.team3.unittests.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import seng202.team3.models.Crash;
import seng202.team3.models.CrashBuilder;

public class CrashStringFormattingTest {
  public CrashBuilder crashBuilder = new CrashBuilder();
  public Crash crash = crashBuilder.build();

  @Test
  void aucklandAddCity() {
    assertEquals("Auckland City", crash.addCityIfAuckland("Auckland"));
  }

  @Test
  void notAucklandDoesntAddCity() {
    assertEquals("Wellington City", crash.addCityIfAuckland("Wellington City"));
  }

  @Test
  void aucklandSetsCorrectly() {
    crashBuilder.setTlaName("auckland");
    crash = crashBuilder.build();
    assertEquals("Auckland City", crash.getTlaName());
  }

  @Test
  void notAucklandSetsCorrectly() {
    crashBuilder.setTlaName("central hawke's bay district");
    crash = crashBuilder.build();
    assertEquals("Central Hawke's Bay District", crash.getTlaName());
  }

  @Test
  void notAucklandDoesntAddCityNullInput() {
    assertEquals(null, crash.addCityIfAuckland(null));
  }

  @Test
  void chopLastWordWithRegion() {
    assertEquals("hawke's bay", crash.chopLastWord("hawke's bay region"));
  }

  @Test
  void chopLastWordNullInput() {
    assertEquals(null, crash.chopLastWord(null));
  }

  @Test
  void getCombinedLocationBothExist() {
    crashBuilder.setCrashLocation1("road");
    crashBuilder.setCrashLocation2("street");
    crash = crashBuilder.build();
    assertEquals("Road, near Street", crash.getLocation());
  }

  @Test
  void getCombinedLocationFirstNull() {
    crashBuilder.setCrashLocation1(null);
    crashBuilder.setCrashLocation2("street");
    crash = crashBuilder.build();
    assertEquals("Street", crash.getLocation());
  }

  @Test
  void getCombinedLocationSecondNull() {
    crashBuilder.setCrashLocation1("road road street avenue");
    crashBuilder.setCrashLocation2(null);
    crash = crashBuilder.build();
    assertEquals("Road Road Street Avenue", crash.getLocation());
  }

  @Test
  void getCombinedLocationBothNull() {
    crashBuilder.setCrashLocation1(null);
    crashBuilder.setCrashLocation2(null);
    crash = crashBuilder.build();
    assertEquals("Unknown", crash.getLocation());
  }

  @Test
  void regionSetsCorrectly() {
    crashBuilder.setRegion("canterbury region");
    crash = crashBuilder.build();
    assertEquals("Canterbury", crash.getRegion());
  }
}
