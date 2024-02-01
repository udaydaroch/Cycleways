package seng202.team3.dao;

import java.sql.*;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.Database;
import seng202.team3.dao.query.QueryBuilder;
import seng202.team3.models.Crash;

/**
 * Class for DAOs for Crash objects. Used for
 *
 * @author Ryan Scofield
 */
public class CrashDAO implements DAOInterface<Crash> {
  private final Database database;
  private static final Logger log = LogManager.getLogger(CrashDAO.class);

  /** Constructor for SaleDAO object, create database reference */
  public CrashDAO() {
    database = Database.getDatabase();
  }

  /**
   * Converts a single row from a ResultSet into a Crash object.
   *
   * @param resultSet The ResultSet from which the Crash data will be extracted.
   * @return A Crash object populated with data from the ResultSet.
   * @throws SQLException if any database access error occurs.
   */
  @Override
  public Crash getOneFromResult(ResultSet resultSet) throws SQLException {
    return new Crash(
        resultSet.getInt("id"),
        resultSet.getInt("bicycle"),
        resultSet.getInt("bus"),
        resultSet.getInt("carStationWagon"),
        resultSet.getInt("moped"),
        resultSet.getInt("motorcycle"),
        resultSet.getInt("schoolBus"),
        resultSet.getInt("suv"),
        resultSet.getInt("taxi"),
        resultSet.getInt("truck"),
        resultSet.getInt("vanOrUtility"),
        resultSet.getInt("otherVehicleType"),
        resultSet.getString("crashLocation1"),
        resultSet.getString("crashLocation2"),
        resultSet.getString("region"),
        resultSet.getString("tlaName"),
        resultSet.getFloat("lng"),
        resultSet.getFloat("lat"),
        resultSet.getInt("crashSeverity"),
        resultSet.getInt("crashYear"),
        resultSet.getInt("fatalInjuryCount"),
        resultSet.getInt("minorInjuryCount"),
        resultSet.getString("light"),
        resultSet.getString("weather"),
        resultSet.getInt("speedLimit"),
        resultSet.getString("roadSurface"),
        resultSet.getString("roadCharacter"),
        resultSet.getInt("roadLane"),
        resultSet.getInt("numberOfLanes"));
  }

  /**
   * Retrieves all the crashes from the database.
   *
   * @return A list of Crash objects, or an empty list if none are found or an error occurs.
   */
  public List<Crash> getAll() {
    return query().getAll();
  }

  /**
   * Take a list of Crash objects and insert all of them into the crash table, or ignore them if
   * they are already in the crash table.
   *
   * @param batch List of Crash objects
   * @return A List of integer IDs of the added records.
   */
  public List<Integer> addBatch(List<Crash> batch) {
    String sql =
        "INSERT OR IGNORE INTO crash (id, bicycle, bus, carStationWagon, moped, motorcycle,"
            + " schoolBus, suv, taxi, truck, vanOrUtility, otherVehicleType, crashLocation1,"
            + " crashLocation2, region, tlaName, lng, lat, crashSeverity, crashYear,"
            + " fatalInjuryCount, minorInjuryCount, light, weather, speedLimit, roadSurface,"
            + " roadCharacter, roadLane, numberOfLanes) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
            + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    List<Integer> addedIds = new ArrayList<>();

    try (Connection conn = database.connect();
        PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      conn.setAutoCommit(false);
      for (int i = 0; i < batch.size(); i++) {
        Crash crash = batch.get(i);
        trySetInt(preparedStatement, 1, crash.getId());
        trySetInt(preparedStatement, 2, crash.getBicycle());
        trySetInt(preparedStatement, 3, crash.getBus());
        trySetInt(preparedStatement, 4, crash.getCarStationWagon());
        trySetInt(preparedStatement, 5, crash.getMoped());
        trySetInt(preparedStatement, 6, crash.getMotorcycle());
        trySetInt(preparedStatement, 7, crash.getSchoolBus());
        trySetInt(preparedStatement, 8, crash.getSuv());
        trySetInt(preparedStatement, 9, crash.getTaxi());
        trySetInt(preparedStatement, 10, crash.getTruck());
        trySetInt(preparedStatement, 11, crash.getVanOrUtility());
        trySetInt(preparedStatement, 12, crash.getOtherVehicleType());
        preparedStatement.setString(13, crash.getCrashLocation1());
        preparedStatement.setString(14, crash.getCrashLocation2());
        preparedStatement.setString(15, crash.getRegion());
        preparedStatement.setString(16, crash.getTlaName());
        preparedStatement.setFloat(17, crash.getLng());
        preparedStatement.setFloat(18, crash.getLat());
        preparedStatement.setInt(19, crash.getCrashSeverity().toInt() - 1);
        trySetInt(preparedStatement, 20, crash.getCrashYear());
        trySetInt(preparedStatement, 21, crash.getFatalInjuryCount());
        trySetInt(preparedStatement, 22, crash.getMinorInjuryCount());
        preparedStatement.setString(23, crash.getLight());
        preparedStatement.setString(24, crash.getWeather());
        trySetInt(preparedStatement, 25, crash.getSpeedLimit());
        preparedStatement.setString(26, crash.getRoadSurface());
        preparedStatement.setString(27, crash.getRoadCharacter());
        trySetInt(preparedStatement, 28, crash.getRoadLane());
        trySetInt(preparedStatement, 29, crash.getNumberOfLanes());
        preparedStatement.addBatch();

        if (i % 100_000 == 0) {
          // ensure that the batch is not bigger than the available memory
          preparedStatement.executeBatch();
        }
      }

      preparedStatement.executeBatch();
      ResultSet rs = preparedStatement.getGeneratedKeys();
      if (rs.next()) {
        addedIds.add(rs.getInt(1));
      }

      conn.setAutoCommit(true);
    } catch (SQLException e) {
      log.error(e);
    }

    return addedIds;
  }

  /**
   * Helper method for setting an integer in the database, either set it as the value or set it as
   * null if said value is null.
   *
   * @param preparedStatement preparedStatement object
   * @param index current index of preparedStatement
   * @param val value of int
   * @throws SQLException ???
   */
  private void trySetInt(PreparedStatement preparedStatement, int index, Integer val)
      throws SQLException {
    try {
      preparedStatement.setInt(index, val);
    } catch (NullPointerException | SQLException e) {
      preparedStatement.setNull(index, Types.INTEGER);
    }
  }

  /**
   * Creates a new QueryBuilder for Crash objects, setting up how to convert ResultSet entries into
   * Crash instances.
   *
   * @return A QueryBuilder tailored for Crash objects.
   */
  public QueryBuilder<Crash> query() {
    return new QueryBuilder<>(this);
  }

  /**
   * Gets all the distinct values in one attribute (column). The results are returned in
   * alphabetical/numerical order
   *
   * @param attribute The attribute/column to get
   * @return list of all the values
   */
  public List<String> getAttributes(String attribute) {
    List<String> items = new ArrayList<String>();

    String sql = "SELECT DISTINCT " + attribute + " FROM crash ORDER BY " + attribute + ";";

    try (Connection connection = database.connect();
        PreparedStatement statement = connection.prepareStatement(sql); ) {
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        if (resultSet.getString(attribute) != null && !resultSet.getString(attribute).isEmpty()) {
          items.add(resultSet.getString(attribute));
        }
      }
      return items;

    } catch (SQLException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  public boolean delete(Crash toDelete) {
    throw new UnsupportedOperationException("Unimplemented method 'delete'");
  }

  /** We are never going to update crashes */
  @Override
  public void update(Crash toUpdate) {
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }

  /** We are never going to add just on crash. */
  @Override
  public Integer add(Crash toAdd) {
    List<Integer> addedIds = addBatch(Arrays.asList(toAdd));
    if (!addedIds.isEmpty()) {
      return addedIds.get(0);
    } else {
      return null;
    }
  }

  @Override
  public String getTableName() {
    return "crash";
  }
}
