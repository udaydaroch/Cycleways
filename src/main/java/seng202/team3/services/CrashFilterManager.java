package seng202.team3.services;

import java.util.Arrays;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;
import seng202.team3.dao.CrashDAO;
import seng202.team3.dao.query.QueryBuilder;
import seng202.team3.models.Crash;

/**
 * Manages crash filter querying contains methods for setting up filter constraints.
 *
 * @author Hanan Fokkens
 */
public class CrashFilterManager {
  private final CrashDAO crashDAO = new CrashDAO();
  private Pair<Integer, Integer> nextYearFilter;
  private Pair<Integer, Integer> nextSpeedFilter;
  private List<String> nextRegionFilter;
  private List<String> nextSeverityFilter;
  private List<String> nextVehiclesInvolvedFilter;
  private List<String> nextWeatherFilter;
  private String nextRoadFilter;
  private Boolean nextOnlyBicycleFilter;
  private final ObjectProperty<QueryBuilder<Crash>> currentQuery =
      new SimpleObjectProperty<>(crashDAO.query());

  public CrashFilterManager() {
    resetFilters();
    applyNextFilters();
  }

  /**
   * Gets the maximum value for the year filter.
   *
   * @return the max value
   */
  public int getYearMaxValue() {
    return 2023;
  }

  /**
   * Gets the minimum value for the year filter.
   *
   * @return the min value
   */
  public int getYearMinValue() {
    return 2000;
  }

  /**
   * Gets the maximum value for the speed filter.
   *
   * @return the max value
   */
  public int getSpeedMaxValue() {
    return 110;
  }

  /**
   * Gets the minimum value for the speed filter.
   *
   * @return the min value
   */
  public int getSpeedMinValue() {
    return 0;
  }

  /**
   * Gets all the values for the severity filter.
   *
   * @return List of possible severities
   */
  public List<String> getSeverityValues() {
    return crashDAO.getAttributes("crashSeverity");
  }

  /**
   * Gets all the values for the region filter.
   *
   * @return List of possible regions.
   */
  public List<String> getRegionValues() {
    return crashDAO.getAttributes("region");
  }

  /**
   * Gets all the values for the weather filter.
   *
   * @return List of possible weather.
   */
  public List<String> getWeatherValues() {
    return crashDAO.getAttributes("weather");
  }

  /**
   * Set the next speed filter to be applied.
   *
   * @param low Lower year bound
   * @param high Upper year bound
   */
  public void setNextYearFilter(Integer low, Integer high) {
    nextYearFilter = new Pair<>(low, high);
  }

  /**
   * Set the next speed filter to be applied.
   *
   * @param low Lower year bound as a double
   * @param high Upper year bound as a double
   */
  public void setNextYearFilter(Double low, Double high) {
    setNextYearFilter((int) Math.floor(low), (int) Math.ceil(high));
  }

  /**
   * Set the next speed filter to be applied.
   *
   * @param low Lower speed bound
   * @param high Upper speed bound
   */
  public void setNextSpeedFilter(Integer low, Integer high) {
    nextSpeedFilter = new Pair<>(low, high);
  }

  /**
   * Set the next speed filter to be applied.
   *
   * @param low Lower speed bound as a double
   * @param high Upper speed bound as a double
   */
  public void setNextSpeedFilter(Double low, Double high) {
    setNextSpeedFilter((int) Math.floor(low), (int) Math.ceil(high));
  }

  /**
   * Sets the next list of regions to be applied as filter.
   *
   * @param regions The list of filtered values
   */
  public void setNextSelectedRegions(List<String> regions) {
    nextRegionFilter = regions;
  }

  /**
   * Sets the next list of severities to be applied as filter.
   *
   * @param severities The list of filtered values
   */
  public void setNextSelectedSeverity(List<String> severities) {
    nextSeverityFilter = severities;
  }

  /**
   * Sets the next list of weather to be applied as filter.
   *
   * @param weather The list of filtered values
   */
  public void setNextSelectedWeather(List<String> weather) {
    nextWeatherFilter = weather;
  }

  /**
   * Set tne next road filter to be applied.
   *
   * @param roadSearch The new value to search for.
   */
  public void setNextRoadFilter(String roadSearch) {
    nextRoadFilter = roadSearch.trim();
  }

  /**
   * Sets the next list of vehicles to be applied as filter.
   *
   * @param vehiclesInvolved The new values for the filter
   */
  public void setNextVehiclesInvolvedFilter(List<String> vehiclesInvolved) {
    nextVehiclesInvolvedFilter = vehiclesInvolved;
  }

  /**
   * Sets the next bicycles only filter.
   *
   * @param onlyBicycleCrashes The next value, true for only bicycles
   */
  public void setNextOnlyBicycleCrashesFilter(Boolean onlyBicycleCrashes) {
    nextOnlyBicycleFilter = onlyBicycleCrashes;
  }

  /** Applies all the next filters and updated the current query. */
  public void applyNextFilters() {
    QueryBuilder<Crash> query =
        crashDAO
            .query()
            .filterBetween("crashYear", nextYearFilter.getKey(), nextYearFilter.getValue())
            .filterBetween("speedLimit", nextSpeedFilter.getKey(), nextSpeedFilter.getValue())
            .filterIn("region", nextRegionFilter)
            .filterLike("weather", nextWeatherFilter)
            .filterIn("crashSeverity", nextSeverityFilter);
    if (!nextRoadFilter.isEmpty())
      query =
          query.filterInColumns(nextRoadFilter, Arrays.asList("crashLocation1", "crashLocation2"));
    if (!nextVehiclesInvolvedFilter.isEmpty()) query.filterNonZero(nextVehiclesInvolvedFilter);
    if (nextOnlyBicycleFilter) query.filterNonZero("bicycle");

    currentQuery.set(query);
  }

  /**
   * Get the observable current query.
   *
   * @return The current query observable.
   */
  public ObjectProperty<QueryBuilder<Crash>> getCurrentQuery() {
    return currentQuery;
  }

  /** Resets all the filters */
  public void resetFilters() {
    setNextOnlyBicycleCrashesFilter(false);
    setNextRoadFilter("");
    setNextSelectedRegions(getRegionValues());
    setNextSelectedSeverity(getSeverityValues());
    setNextSpeedFilter(getSpeedMinValue(), getSpeedMaxValue());
    setNextYearFilter(getYearMinValue(), getYearMaxValue());
    setNextVehiclesInvolvedFilter(Arrays.asList());
  }
}
