package seng202.team3.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.Database;
import seng202.team3.dao.query.QueryBuilder;
import seng202.team3.models.Position;
import seng202.team3.models.SavedRoute;

/**
 * Class for DAOs for Crash objects. Used for
 *
 * @author Hanan
 */
public class SavedRouteDAO implements DAOInterface<SavedRoute> {
  /** Logger instance for logging events and errors. */
  private static final Logger log = LogManager.getLogger(CrashDAO.class);

  private static Database database = Database.getDatabase();

  /**
   * Retrieves a list of saved routes from the database.
   *
   * @return a list of SavedRoute objects representing the saved routes. If an error occurs during
   *     the database fetch, an empty list is returned.
   */
  @Override
  public List<SavedRoute> getAll() {
    return query().getAll();
  }

  /**
   * Makes a SavedRoute from the next result in result set
   *
   * @param resultSet Some results from the saved rout table
   * @return The next saved route from the results
   * @throws SQLException Something went wrong retrieving the values from the result
   */
  @Override
  public SavedRoute getOneFromResult(ResultSet resultSet) throws SQLException {
    return new SavedRoute(
        resultSet.getInt("id"), resultSet.getString("routeStart"), resultSet.getString("routeEnd"));
  }

  /**
   * Retrieves positions of crashes within a bounding box defined by four positions.
   *
   * @param A First position of the bounding box.
   * @param B Second position of the bounding box.
   * @param C Third position of the bounding box.
   * @param D Fourth position of the bounding box.
   * @return A list of positions representing crashes inside the specified bounding box.
   */
  public List<Position> filterBoundingBox(Position A, Position B, Position C, Position D) {
    List<Position> crashesInsideBox = new ArrayList<>();
    double minLat = Collections.min(Arrays.asList(A.getLat(), B.getLat(), C.getLat(), D.getLat()));
    double maxLat = Collections.max(Arrays.asList(A.getLat(), B.getLat(), C.getLat(), D.getLat()));
    double minLng = Collections.min(Arrays.asList(A.getLng(), B.getLng(), C.getLng(), D.getLng()));
    double maxLng = Collections.max(Arrays.asList(A.getLng(), B.getLng(), C.getLng(), D.getLng()));
    String query =
        "SELECT lat, lng FROM crash WHERE (lat BETWEEN %f AND %f) AND (lng BETWEEN %f AND %f)"
            .formatted(minLat, maxLat, minLng, maxLng);
    try (Connection connection = database.connect();
        ResultSet resultSet = connection.createStatement().executeQuery(query)) {
      while (resultSet.next()) {
        Position point = getCrashData(resultSet);
        if (isInsidePolygon(A, B, C, D, point)) {
          crashesInsideBox.add(point);
        }
      }
      return crashesInsideBox;
    } catch (SQLException e) {
      log.error("Error fetching crashes from the database: ", e);
      return Arrays.asList();
    }
  }

  /**
   * Checks if a given position is inside a polygon defined by four position.
   *
   * @param A First position of the polygon.
   * @param B Second position of the polygon.
   * @param C Third position of the polygon.
   * @param D Fourth position of the polygon.
   * @param point The position to check for containment.
   * @return Boolean that is trie if the position is inside the polygon, false otherwise.
   */
  public static boolean isInsidePolygon(
      Position A, Position B, Position C, Position D, Position point) {
    ArrayList<Position> polygonVertices = new ArrayList<>();
    polygonVertices.add(A);
    polygonVertices.add(B);
    polygonVertices.add(C);
    polygonVertices.add(D);
    int n = polygonVertices.size();
    int i = 0;
    int j = n - 1;
    boolean inside = false;
    while (i < n) {
      Position vertexOne = polygonVertices.get(i);
      Position vertexTwo = polygonVertices.get(j);
      double xIntial = vertexOne.getLat();
      double yIntial = vertexOne.getLng();
      double xFinal = vertexTwo.getLat();
      double yFinal = vertexTwo.getLng();
      boolean intersects =
          ((yIntial > point.getLng()) != (yFinal > point.getLng()))
              && (point.getLat()
                  < (xFinal - xIntial) * (point.getLng() - yIntial) / (yFinal - yIntial) + xIntial);
      if (intersects) {
        inside = !inside;
      }
      j = i;
      i += 1;
    }
    return inside;
  }

  /**
   * Creates a Position object from crash data retrieved from a ResultSet.
   *
   * @param resultSet The ResultSet containing crash data.
   * @return A Position object representing the crash data.
   * @throws SQLException If there is an error extracting the crash data from the ResultSet.
   */
  public static Position getCrashData(ResultSet resultSet) throws SQLException {
    return new Position(resultSet.getDouble("lat"), resultSet.getDouble("lng"));
  }

  @Override
  public Integer add(SavedRoute toAdd) {
    String insertSQL = "INSERT INTO route(routeStart, routeEnd) VALUES (?, ?)";
    try (Connection connection = database.connect();
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
      preparedStatement.setString(1, toAdd.getStartLocation());
      preparedStatement.setString(2, toAdd.getEndLocation());
      return preparedStatement.executeUpdate();
    } catch (SQLException e) {
      log.error(e);
      return null;
    }
  }

  @Override
  public boolean delete(SavedRoute toDelete) {
    String deleteSQL = "DELETE FROM route WHERE id = ?";
    try (Connection connection = database.connect();
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
      preparedStatement.setInt(1, toDelete.getId());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      log.error(e);
      return false;
    }
    return true;
  }

  @Override
  public void update(SavedRoute toUpdate) {
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }

  @Override
  public QueryBuilder<SavedRoute> query() {
    return new QueryBuilder<>(this);
  }

  @Override
  public String getTableName() {
    return "route";
  }
}
