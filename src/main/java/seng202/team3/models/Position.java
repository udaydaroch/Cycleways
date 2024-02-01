package seng202.team3.models;

/**
 * @author Morgan English
 */
public class Position {
  private double lat;
  private double lng;

  /**
   * Creates a new position with the provided lat and long
   *
   * @param lat latitude for position
   * @param lng longitude for position
   */
  public Position(double lat, double lng) {
    this.lat = lat;
    this.lng = lng;
  }

  /**
   * get latitude
   *
   * @return latitude
   */
  public double getLat() {
    return this.lat;
  }

  /**
   * get longitude
   *
   * @return longitude
   */
  public double getLng() {
    return this.lng;
  }

  /**
   * Gets the points formatted as JSON.
   *
   * @return The point represented as JSON.
   */
  public String toJSON() {
    return String.format("{\"lat\": %f, \"lng\": %f}", getLat(), getLng());
  }
}
