package seng202.team3.models;

import org.apache.commons.text.WordUtils;
import seng202.team3.models.annotation.Label;

/**
 * Model Crash class
 *
 * @author Ryan Scofield
 * @author Toby Paull
 * @author Hanan Fokkens
 * @author Tom Gallagher
 */
public class Crash {
  @Label(value = "City/District")
  private final String tlaName;

  @Label(value = "Location")
  private final String location;

  @Label(value = "Year")
  private final Integer crashYear;

  @Label(value = "Severity")
  private final Severity crashSeverity;

  @Label(value = "Bicycles")
  private final Integer bicycle;

  @Label(value = "Region")
  private final String region;

  @Label(value = "Speed limit")
  private final Integer speedLimit;

  @Label(value = "Weather")
  private final String weather;

  @Label(value = "Fatal injuries")
  private final Integer fatalInjuryCount;

  @Label(value = "Minor injuries")
  private final Integer minorInjuryCount;

  @Label(value = "Road A")
  private final String crashLocation1;

  @Label(value = "Road B")
  private final String crashLocation2;

  @Label(value = "Id")
  private final int id;

  @Label(value = "Busses")
  private final Integer bus;

  @Label(value = "Cars")
  private final Integer carStationWagon;

  @Label(value = "Mopeds")
  private final Integer moped;

  @Label(value = "Motorcycles")
  private final Integer motorcycle;

  @Label(value = "School busses")
  private final Integer schoolBus;

  @Label(value = "SUVs")
  private final Integer suv;

  @Label(value = "Taxis")
  private final Integer taxi;

  @Label(value = "Trucks")
  private final Integer truck;

  @Label(value = "Vans")
  private final Integer vanOrUtility;

  @Label(value = "Other Vehicles")
  private final Integer otherVehicleType;

  @Label(value = "Longitude")
  private final float lng;

  @Label(value = "Latitude")
  private final float lat;

  @Label(value = "Light level")
  private final String light;

  @Label(value = "Road surface")
  private final String roadSurface;

  @Label(value = "Road character")
  private final String roadCharacter;

  @Label(value = "Road lane")
  private final Integer roadLane;

  @Label(value = "Number of lanes")
  private final Integer numberOfLanes;

  /**
   * Constructor for Crash object with all properties.
   *
   * @param id crash id
   * @param bicycle number of bicycles in this crash
   * @param bus number of buses in this crash
   * @param carStationWagon number of station wagons in this crash
   * @param moped number of mopeds in this crash
   * @param motorcycle number of motorcycles in this crash
   * @param schoolBus number of school buses in this crash
   * @param SUV number of SUVs in this crash
   * @param taxi number of taxis in this crash
   * @param truck number of trucks in this crash
   * @param vanOrUtility number of vans or utility vehicles in this crash
   * @param otherVehicleType number of other vehicles in this crash
   * @param crashLocation1 1st street incident in crash
   * @param crashLocation2 2nd street incident in crash
   * @param region name of region crash was located in
   * @param tlaName name of Territorial Local Authorities crash was located in
   * @param lng longitude
   * @param lat latitude
   * @param crashSeverity string representation of severity of crash
   * @param crashYear year of the crash
   * @param fatalInjuryCount count of fatalities of crash
   * @param minorInjuryCount count of minor injuries of crash
   * @param light string representation of light level
   * @param weather array of weather conditions during crash
   * @param speedLimit speed limit in crash area
   * @param roadSurface descriptor of road surfaces
   * @param roadCharacter descriptor of road type
   * @param roadLane lane type (1-way, 2-way, off-road)
   * @param numberOfLanes number of lanes
   */
  public Crash(
      int id,
      Integer bicycle,
      Integer bus,
      Integer carStationWagon,
      Integer moped,
      Integer motorcycle,
      Integer schoolBus,
      Integer SUV,
      Integer taxi,
      Integer truck,
      Integer vanOrUtility,
      Integer otherVehicleType,
      String crashLocation1,
      String crashLocation2,
      String region,
      String tlaName,
      float lng,
      float lat,
      Integer crashSeverity,
      Integer crashYear,
      Integer fatalInjuryCount,
      Integer minorInjuryCount,
      String light,
      String weather,
      Integer speedLimit,
      String roadSurface,
      String roadCharacter,
      Integer roadLane,
      Integer numberOfLanes) {
    this.id = id;
    this.bicycle = bicycle;
    this.bus = bus;
    this.carStationWagon = carStationWagon;
    this.moped = moped;
    this.motorcycle = motorcycle;
    this.schoolBus = schoolBus;
    this.suv = SUV;
    this.taxi = taxi;
    this.truck = truck;
    this.vanOrUtility = vanOrUtility;
    this.otherVehicleType = otherVehicleType;
    this.crashLocation1 = crashLocation1;
    this.crashLocation2 = crashLocation2;
    this.location = "";
    this.region = chopLastWord(region);
    this.tlaName = addCityIfAuckland(tlaName);
    this.lng = lng;
    this.lat = lat;
    this.crashYear = crashYear;
    this.fatalInjuryCount = fatalInjuryCount;
    this.minorInjuryCount = minorInjuryCount;
    this.light = light;
    this.weather = weather;
    this.speedLimit = speedLimit;
    this.roadSurface = roadSurface;
    this.roadCharacter = roadCharacter;
    this.roadLane = roadLane;
    this.numberOfLanes = numberOfLanes;

    Severity crashSeverityTemp = Severity.NULL;
    if (crashSeverity != null) {
      switch (crashSeverity) {
        case 0 -> crashSeverityTemp = Severity.NON_INJURY;
        case 1 -> crashSeverityTemp = Severity.MINOR_INJURY;
        case 2 -> crashSeverityTemp = Severity.SERIOUS_INJURY;
        case 3 -> crashSeverityTemp = Severity.FATAL_CRASH;
      }
    }
    this.crashSeverity = crashSeverityTemp;
  }

  /**
   * get id
   *
   * @return id
   */
  public int getId() {
    return id;
  }

  /**
   * get bicycle
   *
   * @return # bicycles in crash
   */
  public Integer getBicycle() {
    return bicycle;
  }

  /**
   * get buses
   *
   * @return # buses in crash
   */
  public Integer getBus() {
    return bus;
  }

  /**
   * get carStationWagon
   *
   * @return # cars or station wagons in crash
   */
  public Integer getCarStationWagon() {
    return carStationWagon;
  }

  /**
   * get moped
   *
   * @return # mopeds in crash
   */
  public Integer getMoped() {
    return moped;
  }

  /**
   * get motorcycle
   *
   * @return # motorcycles in crash
   */
  public Integer getMotorcycle() {
    return motorcycle;
  }

  /**
   * get schoolBus
   *
   * @return # school buses in crash
   */
  public Integer getSchoolBus() {
    return schoolBus;
  }

  /**
   * get suv
   *
   * @return # suvs in crash
   */
  public Integer getSuv() {
    return suv;
  }

  /**
   * get taxi
   *
   * @return # taxis in crash
   */
  public Integer getTaxi() {
    return taxi;
  }

  /**
   * get truck
   *
   * @return # trucks in crash
   */
  public Integer getTruck() {
    return truck;
  }

  /**
   * get vanOrUtility
   *
   * @return # vans or utility vehicles in crash
   */
  public Integer getVanOrUtility() {
    return vanOrUtility;
  }

  /**
   * get otherVehicleType
   *
   * @return # other vehicles in crash
   */
  public Integer getOtherVehicleType() {
    return otherVehicleType;
  }

  /**
   * get crashLocation1
   *
   * @return 1st location of crash
   */
  public String getCrashLocation1() {
    if (crashLocation1 != null) {
      return WordUtils.capitalize(crashLocation1);
    } else {
      return null;
    }
  }

  /**
   * get crashLocation2
   *
   * @return 2nd location of crash
   */
  public String getCrashLocation2() {
    if (crashLocation2 != null) {
      return WordUtils.capitalize(crashLocation2);
    } else {
      return null;
    }
  }
  /**
   * get location
   *
   * @return combination string of crashLocation1 and crashLocation2
   */
  public String getLocation() {
    return getCombinationLocation();
  }

  /**
   * get region
   *
   * @return region of crash
   */
  public String getRegion() {
    if (region != null) {
      return WordUtils.capitalizeFully(region);
    } else {
      return null;
    }
  }

  /**
   * get tlaName
   *
   * @return tla of crash
   */
  public String getTlaName() {
    if (tlaName != null) {
      return WordUtils.capitalizeFully(tlaName);
    } else {
      return null;
    }
  }

  /**
   * get lng
   *
   * @return longitude of crash
   */
  public float getLng() {
    return lng;
  }

  /**
   * get lat
   *
   * @return latitude of crash
   */
  public float getLat() {
    return lat;
  }

  /**
   * get crashSeverity
   *
   * @return severity of crash
   */
  public Severity getCrashSeverity() {
    return crashSeverity;
  }

  /**
   * get crashYear
   *
   * @return year of crash
   */
  public Integer getCrashYear() {
    return crashYear;
  }

  /**
   * get fatalInjuryCount
   *
   * @return # fatal injuries in crash
   */
  public Integer getFatalInjuryCount() {
    return fatalInjuryCount;
  }

  /**
   * get minorInjuryCount
   *
   * @return # minor injuries in crash
   */
  public Integer getMinorInjuryCount() {
    return minorInjuryCount;
  }

  /**
   * get light
   *
   * @return light level during crash
   */
  public String getLight() {
    return light;
  }

  /**
   * get weather
   *
   * @return weather during crash
   */
  public String getWeather() {
    return weather;
  }

  /**
   * get speedLimit
   *
   * @return speedLimit around crash
   */
  public Integer getSpeedLimit() {
    return speedLimit;
  }

  /**
   * get roadSurface
   *
   * @return roadSurface around crash
   */
  public String getRoadSurface() {
    return roadSurface;
  }

  /**
   * get roadCharacter
   *
   * @return road character around crash
   */
  public String getRoadCharacter() {
    return roadCharacter;
  }

  /**
   * get roadLane
   *
   * @return road lane of crash
   */
  public Integer getRoadLane() {
    return roadLane;
  }

  /**
   * get numberOfLanes
   *
   * @return number of lanes around crash
   */
  public Integer getNumberOfLanes() {
    return numberOfLanes;
  }

  /**
   * Used only by test to create a crash containing dummy information
   *
   * @param id The id to give this crash
   * @param severity The severity to set
   * @return Crash object with dummy data
   */
  public static Crash createTestCrash(Integer id, Integer severity) {
    return new Crash(
        id,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        "Ilam Road",
        "Maidstone road",
        "Canterbury",
        "Christchurch",
        -43.51972663517813f,
        172.5790732450922f,
        severity,
        2023,
        12,
        42,
        "",
        "",
        120,
        "Christchurch road surface",
        "Lucky if you can drive on it",
        3,
        3);
  }

  /**
   * gets a string representing the location, which a combination of crashLocation1 and
   * crashLocation2
   *
   * @return the location string
   */
  private String getCombinationLocation() {
    if (crashLocation1 == null && crashLocation2 == null) {
      return "Unknown";
    } else if (crashLocation1 == null) {
      return WordUtils.capitalizeFully(crashLocation2);
    } else if (crashLocation2 == null) {
      return WordUtils.capitalizeFully(crashLocation1);
    } else {
      return WordUtils.capitalizeFully(crashLocation1)
          + ", near "
          + WordUtils.capitalizeFully(crashLocation2);
    }
  }

  /**
   * removes the last word from a string
   *
   * @param input the string to ave the last word removed from
   * @return the new string without the last word
   */
  public String chopLastWord(String input) {
    if (input == null) {
      return null;
    }
    String[] output = input.split(" ");
    output[output.length - 1] = "";
    return String.join(" ", output).strip();
  }

  /**
   * adds " City" to the tla name if it is auckland
   *
   * @param tla the tla name
   * @return the changed or unchanged string
   */
  public String addCityIfAuckland(String tla) {
    if (tla == null) {
      return null;
    }
    if (tla.toLowerCase().strip().equals("auckland")) {
      return tla + " City";
    } else {
      return tla;
    }
  }

  @Override
  public String toString() {
    return "Crash " + Integer.toString(getId());
  }
}
