package seng202.team3;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.exceptions.InstanceAlreadyExistsException;

/**
 * Singleton Facade to the database connection.
 *
 * @author Ryan Scofield
 * @author Hanan Fokkens
 * @author Morgan English
 */
public class Database {
  private static Database database = null;
  private static final Logger log = LogManager.getLogger(Database.class);
  private final String url;

  /**
   * Private database constructor used to create a new instance of Database. Only used by
   * getDatabase since this is a singleton.
   *
   * @param path The path to the database file that should be used for the app
   */
  private Database(String path) {
    url = "jdbc:sqlite:" + path;

    boolean fileExists = new File(url.substring(12)).exists();

    if (!fileExists) {
      // make the file
      recreate();
    }
  }

  /** Re-creates all tables. */
  private void recreate() {
    try {
      InputStream in = getClass().getResourceAsStream("/sql/create_crash_table.sql");
      InputStream bn = getClass().getResourceAsStream("/sql/create_journey_table.sql");
      executeSQLScript(in);
      executeSQLScript(bn);
    } catch (NullPointerException e) {
      log.error(e);
    }
  }
  /**
   * Get the path `database.db` that is next to the built jar file
   *
   * @return String containing the path to the db file.
   */
  private static String getDefaultPath() {
    URL jarLocation = Database.class.getProtectionDomain().getCodeSource().getLocation();
    String path = URLDecoder.decode(jarLocation.getPath(), StandardCharsets.UTF_8);

    return path.substring(0, path.lastIndexOf("/") + 1) + "database.db";
  }

  /**
   * Reads each line of SQL file and splits into many statements which are subsequently executed.
   *
   * @param sqlFile input stream of SQL file
   */
  private void executeSQLScript(InputStream sqlFile) {
    String currentLine;
    StringBuffer sqlLines = new StringBuffer();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(sqlFile))) {
      while ((currentLine = br.readLine()) != null) {
        sqlLines.append(currentLine);
      }

      String sql = sqlLines.toString();

      log.info(sql);
      try (Connection conn = this.connect();
          Statement statement = conn.createStatement()) {
        statement.executeUpdate(sql);
      }

      // String[] allStatements = sqlLines.toString().split("--SPLIT");
    } catch (IOException | SQLException e) {
      log.error(e);
    }
  }

  /**
   * connects to database
   *
   * @return connection to database
   */
  public Connection connect() {
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(this.url);
    } catch (SQLException e) {
      log.error(e);
    }
    return conn;
  }

  /**
   * Used to get the instance that is in use, make a new instance on first call.
   *
   * @param path Path to the database file. When this is null it auto generates a path based on the
   *     jar.
   * @return Current isntance for this app
   */
  private static Database getDatabase(String path) {
    if (database == null) {
      database = new Database(path);
    }

    return database;
  }

  /**
   * Gets an instance of a database with a specific path.
   *
   * @param path the path to open
   * @return the approprate database instance in use
   * @throws InstanceAlreadyExistsException If a Database instance already exists and attempting to set a new path.
   */
  public static Database getDatabaseWithPath(String path) throws InstanceAlreadyExistsException {
    if (database != null) {
      throw new InstanceAlreadyExistsException("Database instance already set, can't set path");
    }
    return getDatabase(path);
  }

  /**
   * Gets an instance of the database using the default path - if not already instantiated
   *
   * @return database instance with default path
   */
  public static Database getDatabase() {
    return getDatabase(getDefaultPath());
  }

  /** Clears whatever instance was in use so another can be set. */
  public static void RESET() {
    database = null;
  }
}
