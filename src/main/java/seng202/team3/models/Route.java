package seng202.team3.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple class representing a route as any number of positions
 *
 * @author Morgan English
 */
public class
Route {
  private List<Position> positions = new ArrayList<>();
  private Double routeDistance;
  private Integer routeDuration;
  private Integer numberOfRouteCrashes;
  private Integer routeRating;
  private Integer routeId;
  private ArrayList<String> routeDirections;

  private Double routeElivation;

  private Double averageCrashPerKmOnRoute;
  /**
   * Create a new route with any number of positions
   *
   * @param points points along the route in order first to last
   */
  public Route(Integer id,
               Integer rating,
               Double distance,
               Integer duration,
               Integer crashesOnRoute,
               ArrayList<String> directions,
               Double elevation,
               Double averageCrashesPerKm,
               Position... points) {
    Collections.addAll(positions, points); 
    routeRating = rating;
    routeDistance = distance;
    routeDuration = duration;
    numberOfRouteCrashes = crashesOnRoute;
    routeDirections = directions;
    routeElivation = elevation;
    averageCrashPerKmOnRoute = averageCrashesPerKm;
    routeId = id;
  }
  public int getId() {return routeId;};

  /**
   * gets the average crashs per km on the route.
   * @return averageCrashPerKmOnRoute
   */
  public Double getAverageCrashPerKmOnRoute() {return averageCrashPerKmOnRoute;};

  /**
   * this sets the Average Crashes per km on route.
   * @param averageCrashes sets the value of the averageCrashperkmonroute variable.
   */
  public void setAverageCrashPerKmOnRoute(Double averageCrashes) {
    averageCrashPerKmOnRoute = averageCrashes;
  }

  /**
   * returns the average elivation of that route.
   * @return routeElivation
   */
  public Double getElivation(){return routeElivation;};

  /**
   * this gets the instructions of the route.
   * @return a list of directions
   */
  public List<String> getInstruction() {return routeDirections;}

  /**
   * this gets the rating of the route.
   * @return routeRating rating of the route
   */

  public int getRating() {return routeRating; }

  /**
   * sets the rating of the route.
   * @param rating
   */
  public void setRating(int rating) {routeRating = rating;}

  /**
   * sets the route distance
   * @return routeDistance
   */
  public Double getRouteDistance() { return routeDistance; }

  /**
   * gets the route duration
   * @return routeDuration
   */
  public long getRouteDuration() {return routeDuration;}

  /**
   * gets the route crashes
   * @return numberOfRouteCrashes
   */
  public int getRouteCrashes() {return numberOfRouteCrashes; }

  /**
  sets the route crashes
   @param crashes which provides the number of crashes on that route.
   */
  public void setRouteCrashes(int crashes) {numberOfRouteCrashes = crashes;}

  /**
   * Gets all the points stored in this route
   *
   * @return List of points
   */
  public List<Position> getPoints() {
    return positions;
  }

  /**
   * Gets the start point of the route.
   *
   * @return Position for the start point
   */
  public Position getStart() {
    return positions.get(0);
  }

  /**
   * Gets the end point of the route
   *
   * @return Position for the end point
   */
  public Position getEnd() {
    return positions.get(positions.size() - 1);
  }

  /**
   * Get the number of points stored.
   *
   * @return int for the number of points on route
   */
  public int length() {
    return positions.size();
  }

  /**
   * Returns the route as a JSON array
   *
   * @return route object as JSON array
   */
  public String toJSONArray() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("[");
    for (int i = 0; i < positions.size(); i++) {
      stringBuilder.append(positions.get(i).toJSON());

      if (i < positions.size() - 1) {
        // if this is not the last i add a comma to separate them
        stringBuilder.append(", ");
      }
    }
    stringBuilder.append("]");
    return stringBuilder.toString();
  }
}
