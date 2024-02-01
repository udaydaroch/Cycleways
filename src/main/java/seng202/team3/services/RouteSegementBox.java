package seng202.team3.services;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.models.Position;

/**
 * Class that creates boxes around a given route in order to display crashes and perform route
 * analysis
 *
 * @author Uday Daroch
 */
public class RouteSegementBox {
  private static final Logger log = LogManager.getLogger(RouteSegementBox.class);
  Position startLocation;
  Position endLocation;
  HashMap<ArrayList<Position>, ArrayList<Position>> rotatedBox = new HashMap<>();

  /**
   * Constructor to create a RouteSegmentBox with given start and end locations.
   *
   * @param startloc The start location of the route segment.
   * @param endloc The end location of the route segment.
   */
  public RouteSegementBox(Position startloc, Position endloc) {
    startLocation = startloc;
    endLocation = endloc;
  }

  /**
   * Calculates the angle between two positions.
   *
   * @param point1 The first position.
   * @param point2 The second position.
   * @return The angle between the positions in radians.
   */
  public Double calculateAngle(Position point1, Position point2) {
    log.info("RouteBox Object calculated Angle");
    return Math.atan2(point2.getLat() - point1.getLat(), point2.getLng() - point1.getLng());
  }

  /**
   * Calculates the coordinates for the bounding box based on the segment's start and end locations.
   *
   * @return hashmap containing the rotated bounding box coordinates.
   */
  public HashMap<ArrayList<Position>, ArrayList<Position>> boxCoordinateList() {
    double distance = calculateDistance(startLocation, endLocation);
    Double angle = calculateAngle(startLocation, endLocation);
    double halfWidth = distance * 0.0005;
    double halfHeight = 0.0004;
    double cosAngle = Math.cos(angle);
    double sinAngle = Math.sin(angle);

    double lat1 =
        Math.round((startLocation.getLat() - halfHeight * cosAngle - halfWidth * sinAngle) * 1e6)
            / 1e6;
    double lng1 =
        Math.round((startLocation.getLng() - halfWidth * cosAngle + halfHeight * sinAngle) * 1e6)
            / 1e6;
    double lat2 =
        Math.round((startLocation.getLat() + halfHeight * cosAngle - halfWidth * sinAngle) * 1e6)
            / 1e6;
    double lng2 =
        Math.round((startLocation.getLng() - halfWidth * cosAngle - halfHeight * sinAngle) * 1e6)
            / 1e6;
    double lat3 =
        Math.round((endLocation.getLat() + halfHeight * cosAngle + halfWidth * sinAngle) * 1e6)
            / 1e6;
    double lng3 =
        Math.round((endLocation.getLng() + halfWidth * cosAngle - halfHeight * sinAngle) * 1e6)
            / 1e6;
    double lat4 =
        Math.round((endLocation.getLat() - halfHeight * cosAngle + halfWidth * sinAngle) * 1e6)
            / 1e6;
    double lng4 =
        Math.round((endLocation.getLng() + halfWidth * cosAngle + halfHeight * sinAngle) * 1e6)
            / 1e6;

    ArrayList<Position> boxCoord = new ArrayList<>();
    boxCoord.add(new Position(lat1, lng1));
    boxCoord.add(new Position(lat2, lng2));
    boxCoord.add(new Position(lat3, lng3));
    boxCoord.add(new Position(lat4, lng4));
    ArrayList<Position> twoPoints = new ArrayList<>();
    twoPoints.add(startLocation);
    twoPoints.add(endLocation);
    rotatedBox.put(boxCoord, twoPoints);
    return rotatedBox;
  }

  private double calculateDistance(Position point1, Position point2) {
    double earthRadius = 6371;

    double lat1 = Math.toRadians(point1.getLat());
    double lon1 = Math.toRadians(point1.getLng());
    double lat2 = Math.toRadians(point2.getLat());
    double lon2 = Math.toRadians(point2.getLng());

    double dLat = lat2 - lat1;
    double dLon = lon2 - lon1;

    double a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return earthRadius * c;
  }
}
