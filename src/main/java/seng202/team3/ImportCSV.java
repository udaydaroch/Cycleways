package seng202.team3;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import seng202.team3.models.Crash;
import seng202.team3.models.CrashBuilder;

/**
 * Class for importing CSV data into Crash objects.
 *
 * @author Ryan Scofield
 * @author Hanan Fokkens
 */
public class ImportCSV {

  /**
   * Read each line of file and append it to ArrayList.
   *
   * @param filename name of the csv file
   * @return arraylist of Crash objects
   */
  public List<Crash> readAllDataFromFile(String filename) {
    File csv = new File(filename);

    try (CSVReader reader = new CSVReader(new FileReader(csv))) {
      Map<String, Integer> headerMap = new HashMap<>();
      String[] header = reader.readNext();
      for (int i = 0; i < header.length; i++) {
        headerMap.put(header[i], i);
      }

      List<Crash> crashList = new ArrayList<>();
      String[] line;
      while ((line = reader.readNext()) != null) {
        Crash crash = getCrashFromLine(line, headerMap);
        if (crash != null) crashList.add(crash);
      }

      return crashList;

    } catch (IOException | CsvValidationException e) {
      throw new IllegalArgumentException("invalid file");
    }
  }

  /**
   * Take a line from the csv and return a crash object which stores said data.
   *
   * @param line current line to be read
   * @param headerMap mapping of csv col name : index
   * @return Crash object or null for invalid line
   */
  private Crash getCrashFromLine(String[] line, Map<String, Integer> headerMap) {
    if (line[headerMap.get("OBJECTID")].isBlank()) {
      return null;
    }

    String weather =
        !line[headerMap.get("weatherA")].equalsIgnoreCase("null")
            ? line[headerMap.get("weatherA")].toLowerCase()
            : "";
    weather +=
        !weather.isEmpty() && !line[headerMap.get("weatherB")].equalsIgnoreCase("null")
            ? " & "
            : "";
    weather +=
        !line[headerMap.get("weatherB")].equalsIgnoreCase("null")
            ? line[headerMap.get("weatherB")].toLowerCase()
            : "";
    weather = !weather.isEmpty() ? weather : "null";

    CrashBuilder crashBuilder =
        new CrashBuilder()
            .setId(getInt(line[headerMap.get("OBJECTID")]))
            .setBicycle(getInt(line[headerMap.get("bicycle")]))
            .setBus(getInt(line[headerMap.get("bus")]))
            .setCarStationWagon(getInt(line[headerMap.get("carStationWagon")]))
            .setMoped(getInt(line[headerMap.get("moped")]))
            .setMotorcycle(getInt(line[headerMap.get("motorcycle")]))
            .setSchoolBus(getInt(line[headerMap.get("schoolBus")]))
            .setSuv(getInt(line[headerMap.get("suv")]))
            .setTaxi(getInt(line[headerMap.get("taxi")]))
            .setTruck(getInt(line[headerMap.get("truck")]))
            .setVanOrUtility(getInt(line[headerMap.get("vanOrUtility")]))
            .setOtherVehicleType(getInt(line[headerMap.get("otherVehicleType")]))
            .setCrashLocation1(getString(line[headerMap.get("crashLocation1")].toLowerCase()))
            .setCrashLocation2(getString(line[headerMap.get("crashLocation2")].toLowerCase()))
            .setRegion(getString(line[headerMap.get("region")].toLowerCase()))
            .setTlaName(getString(line[headerMap.get("tlaName")].toLowerCase()))
            .setLng(Float.parseFloat(line[headerMap.get("lng")]))
            .setLat(Float.parseFloat(line[headerMap.get("lat")]))
            .setCrashSeverity(severityToInteger(line[headerMap.get("crashSeverity")]))
            .setCrashYear(getInt(line[headerMap.get("crashYear")]))
            .setFatalInjuryCount(getInt(line[headerMap.get("fatalCount")]))
            .setMinorInjuryCount(getInt(line[headerMap.get("minorInjuryCount")]))
            .setLight(getString(line[headerMap.get("light")].toLowerCase()))
            .setWeather(weather)
            .setSpeedLimit(getInt(line[headerMap.get("speedLimit")]))
            .setRoadSurface(getString(line[headerMap.get("roadSurface")].toLowerCase()))
            .setRoadCharacter(getString(line[headerMap.get("roadCharacter")].toLowerCase()))
            .setRoadLane(getInt(line[headerMap.get("roadLane")]))
            .setNumberOfLanes(getInt(line[headerMap.get("NumberOfLanes")]));

    return crashBuilder.build();
  }

  /**
   * Deal with integers, in the case that field is null return null.
   *
   * @param stringInt string representation of an int
   * @return int value
   */
  private Integer getInt(String stringInt) {
    try {
      return Integer.parseInt(stringInt);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /**
   * convert string to string dealing with nulls
   * @param stringInput string to be dealt with
   * @return trimmed string or null
   */
  private String getString(String stringInput) {
    if (stringInput.isEmpty()) {
      return null;
    } else {
      return stringInput.trim();
    }
  }

  /**
   * convert severity string in csv to integer value
   * @param severity string representation of severity
   * @return Integer value of string
   */
  private Integer severityToInteger(String severity) {
    return switch (severity) {
      case "Non-Injury Crash" -> 0;
      case "Minor Crash" -> 1;
      case "Serious Crash" -> 2;
      case "Fatal Crash" -> 3;
      default -> null;
    };
  }
}
