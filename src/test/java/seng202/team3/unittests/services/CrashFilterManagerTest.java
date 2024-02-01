package seng202.team3.unittests.services;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seng202.team3.App;
import seng202.team3.Database;
import seng202.team3.exceptions.InstanceAlreadyExistsException;
import seng202.team3.services.CrashFilterManager;

public class CrashFilterManagerTest {
  @TempDir static Path tempDir;
  CrashFilterManager crashFilterManager;

  @BeforeEach
  void beforeEach() {
    crashFilterManager = new CrashFilterManager();
  }

  @BeforeAll
  static void beforeAll() throws InstanceAlreadyExistsException {
    Database.RESET();
    Database.getDatabaseWithPath(tempDir.resolve("database.db").toString());

    // load in the 10k
    App.main(new String[] {"load", "./crash_data_10k.csv"});
  }

  @Test
  void getYearMaxValue__is2023() {
    Assertions.assertEquals(2023, crashFilterManager.getYearMaxValue());
  }

  @Test
  void getYearMinValue__is2000() {
    Assertions.assertEquals(2000, crashFilterManager.getYearMinValue());
  }

  @Test
  void getSpeedMinValue__is0() {
    Assertions.assertEquals(0, crashFilterManager.getSpeedMinValue());
  }

  @Test
  void getSpeedMaxValue__is110() {
    Assertions.assertEquals(110, crashFilterManager.getSpeedMaxValue());
  }

  @Test
  void getSeverityValues__isCorrect() {
    Assertions.assertEquals(
        Arrays.asList("0", "1", "2", "3"), crashFilterManager.getSeverityValues());
  }

  @Test
  void getRegionValues__isCorrect() {
    Assertions.assertEquals(
        Arrays.asList(
            "Auckland",
            "Bay Of Plenty",
            "Canterbury",
            "Gisborne",
            "Hawke's Bay",
            "Manawatū-whanganui",
            "Marlborough",
            "Nelson",
            "Northland",
            "Otago",
            "Southland",
            "Taranaki",
            "Tasman",
            "Waikato",
            "Wellington",
            "West Coast"),
        crashFilterManager.getRegionValues());
  }

  @Test
  void getWeatherValues__isCorrect() {
    Assertions.assertEquals(
        Arrays.asList(
            "fine",
            "fine & frost",
            "fine & strong wind",
            "frost",
            "hail or sleet",
            "heavy rain",
            "heavy rain & strong wind",
            "light rain",
            "light rain & frost",
            "light rain & strong wind",
            "mist or fog",
            "mist or fog & frost",
            "null",
            "snow",
            "snow & frost",
            "snow & strong wind",
            "strong wind"),
        crashFilterManager.getWeatherValues());
  }

  @Test
  void usingSetNextMethods__DoesNotChangeQuery() {
    String previous = crashFilterManager.getCurrentQuery().getValue().getAllString();
    crashFilterManager.setNextOnlyBicycleCrashesFilter(false);
    crashFilterManager.setNextRoadFilter("Ilam Road");
    crashFilterManager.setNextSelectedSeverity(List.of("Fatal Crash"));
    crashFilterManager.setNextSelectedRegions(List.of("Canterbury"));
    crashFilterManager.setNextSpeedFilter(10, 100);
    crashFilterManager.setNextYearFilter(2002, 2020);
    crashFilterManager.setNextVehiclesInvolvedFilter(List.of("car"));

    assertEquals(previous, crashFilterManager.getCurrentQuery().getValue().getAllString());
  }

  @Test
  void applyNextFilters__ChangesQuery() {
    crashFilterManager.setNextOnlyBicycleCrashesFilter(false);
    crashFilterManager.setNextRoadFilter("Ilam Road");
    crashFilterManager.setNextSelectedSeverity(List.of("Fatal Crash"));
    crashFilterManager.setNextSelectedRegions(List.of("Canterbury"));
    crashFilterManager.setNextSpeedFilter(10, 100);
    crashFilterManager.setNextYearFilter(2002, 2020);
    crashFilterManager.setNextVehiclesInvolvedFilter(List.of("car"));

    crashFilterManager.applyNextFilters();
    Assertions.assertEquals(
        "SELECT * FROM crash WHERE crashYear BETWEEN ? AND ? AND speedLimit BETWEEN ? AND ? AND"
            + " region IN (?) AND TRUE AND crashSeverity IN (?) AND ((crashLocation1 LIKE ?) OR"
            + " (crashLocation2 LIKE ?)) AND car > 0;",
        crashFilterManager.getCurrentQuery().getValue().getAllString());
  }
}
