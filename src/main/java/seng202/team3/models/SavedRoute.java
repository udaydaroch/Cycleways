package seng202.team3.models;

import java.util.Objects;

/** Represents a saved route with a starting and ending location. */
public class SavedRoute {
  private Integer id;
  private String startLocation;
  private String endLocation;

  /**
   * Constructs a new SavedRoute with the specified starting and ending locations.
   *
   * @param startLocation The starting location of the route.
   * @param endLocation The ending location of the route.
   */
  public SavedRoute(String startLocation, String endLocation) {
    this.startLocation = startLocation;
    this.endLocation = endLocation;
  }

  /**
   * Constructs a new SavedRoute with the specified starting and ending locations.
   *
   * @param id the id
   * @param startLocation The starting location of the route.
   * @param endLocation The ending location of the route.
   */
  public SavedRoute(int id, String startLocation, String endLocation) {
    this.id = id;
    this.startLocation = startLocation;
    this.endLocation = endLocation;
  }

  /**
   * Get the Id for the saved route will be none if this hasn't been saved yet.
   *
   * @return the id for the saved route, null if did not come from database.
   */
  public Integer getId() {
    return id;
  }

  /**
   * Retrieves the starting location of the route.
   *
   * @return The starting location.
   */
  public String getStartLocation() {
    return startLocation;
  }

  /**
   * Retrieves the ending location of the route.
   *
   * @return The ending location.
   */
  public String getEndLocation() {
    return endLocation;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    SavedRoute that = (SavedRoute) obj;
    return Objects.equals(startLocation, that.startLocation)
        && Objects.equals(endLocation, that.endLocation);
  }

  /**
   * Returns a string representation of the route, formatted as "startLocation to endLocation".
   *
   * @return A string representation of the route.
   */
  @Override
  public String toString() {
    return "From " + startLocation + System.lineSeparator() + "To " + endLocation;
  }
}
