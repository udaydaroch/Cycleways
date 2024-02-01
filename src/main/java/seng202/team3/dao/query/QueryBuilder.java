package seng202.team3.dao.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.Database;
import seng202.team3.dao.DAOInterface;

/**
 * QueryBuilder used to build complicated queries and then get the results in different ways.
 *
 * @author Hanan Fokkens
 * @author Tom Gallagher
 * @author Toby Paull
 * @author Uday Daroch
 */
public class QueryBuilder<T> implements Cloneable {
  private List<String> order = new ArrayList<>();
  private List<String> conditions = new ArrayList<>();
  private List<String> conditionParams = new ArrayList<>();
  private final Database database;
  private static final Logger log = LogManager.getLogger(QueryBuilder.class);
  private final DAOInterface<T> dao;

  /**
   * Gets a new instance of QueryBuilder
   *
   * @param dao The DAO related to this query builder
   */
  public QueryBuilder(DAOInterface<T> dao) {
    this.dao = dao;
    this.database = Database.getDatabase();
  }

  /**
   * Sort the query by a column in ascending order unless reverse is set.
   *
   * @param column Column name
   * @param reverse Reverse the sort order
   * @return Instance of query builder used
   */
  public QueryBuilder<T> sort(String column, Boolean reverse) {
    if (reverse) {
      order.add(column + " DESC");
    } else {
      order.add(column + " ASC NULLS LAST");
    }
    return this;
  }

  /**
   * Sort the query by a column in ascending order.
   *
   * @param column Column name
   * @return Instance of query builder used
   */
  public QueryBuilder<T> sort(String column) {
    return sort(column, false);
  }

  /**
   * Filter a column to a string value.
   *
   * @param column The column to filter
   * @param value The expected string
   * @return Instance of query builder used
   */
  public QueryBuilder<T> filterEqual(String column, String value) {
    conditions.add(String.format("%s = ?", column, value));
    conditionParams.add(value);
    return this;
  }

  /**
   * Filter a column to a number value.
   *
   * @param column The column to filter
   * @param value The expected number
   * @return Instance of query builder used
   */
  public QueryBuilder<T> filterEqual(String column, Number value) {
    return filterEqual(column, value.toString());
  }

  /**
   * Filter column greater than zero.
   *
   * @param column The column to filter
   * @return Instance of query builder used
   */
  public QueryBuilder<T> filterNonZero(String column) {
    conditions.add(String.format("%s > 0", column));
    return this;
  }

  /**
   * @see QueryBuilder#filterNonZero(List)
   * @param columns List of columns to be filtered non-zero
   * @return Instance of query builder used
   */
  public QueryBuilder<T> filterNonZero(List<String> columns) {
    for (String column : columns) {
      this.filterNonZero(column);
    }
    return this;
  }

  /**
   * Filter a column to a range of value(using the SQL BETWEEN OPERATOR)
   *
   * @param column The column chosen to filter
   * @param start The start string of the range that is being chosen
   * @param end The end string of the range that is being chosen
   * @return Instance of query builder used
   */
  public QueryBuilder<T> filterBetween(String column, String start, String end) {
    conditions.add(String.format("%s BETWEEN ? AND ?", column));
    conditionParams.add(start);
    conditionParams.add(end);
    return this;
  }

  /**
   * Filter a column to a range of value(using the SQL BETWEEN OPERATOR)
   *
   * @param column The column chosen to filter
   * @param low The start number of the range that is being chosen
   * @param high The end number of the range that is being chosen
   * @return Instance of query builder used
   */
  public QueryBuilder<T> filterBetween(String column, Number low, Number high) {
    return filterBetween(column, low.toString(), high.toString());
  }

  /**
   * Filters the query based on a bounding box defined by four latitude-longitude pairs.
   *
   * @param lat1 The minimum latitude of the bounding box.
   * @param lon1 The minimum longitude of the bounding box.
   * @param lat2 The maximum latitude of the bounding box.
   * @param lon2 The maximum longitude of the bounding box.
   * @param lat3 Additional latitude point for bounding box.
   * @param lon3 Additional longitude point for bounding box.
   * @param lat4 Additional latitude point for bounding box.
   * @param lon4 Additional longitude point for bounding box.
   * @return The QueryBuilder instance for further chaining.
   */
  public QueryBuilder<T> filterBoundingBox(
      double lat1,
      double lon1,
      double lat2,
      double lon2,
      double lat3,
      double lon3,
      double lat4,
      double lon4) {
    conditions.add("latitude BETWEEN ? AND ? AND longitude BETWEEN ? AND ?");
    conditionParams.add(String.valueOf(Math.min(lat1, lat3)));
    conditionParams.add(String.valueOf(Math.max(lat2, lat4)));
    conditionParams.add(String.valueOf(Math.min(lon1, lon2)));
    conditionParams.add(String.valueOf(Math.max(lon3, lon4)));
    return this;
  }

  /**
   * Filters a column - must be in set of allowed values.
   *
   * @param column the column to filer
   * @param values the allowed values
   * @param not Flag to invert filter in, making it filter not in
   * @return Instance of query builder used
   */
  public QueryBuilder<T> filterIn(String column, List<String> values, Boolean not) {
    if (values == null || values.isEmpty()) {
      // if there are no values it is best just to add a FALSE
      conditions.add("TRUE");
      return this;
    }

    // there will always be at least one parameter
    StringBuilder parameters = new StringBuilder("?");
    conditionParams.add(values.get(0));

    // if there are more than one, this for loop adds the remaining
    for (int i = 1; i < values.size(); i++) {
      parameters.append(", ?");
      conditionParams.add(values.get(i));
    }

    if (not) {
      conditions.add("%s NOT IN (%s)".formatted(column, parameters.toString()));
    } else {
      conditions.add("%s IN (%s)".formatted(column, parameters.toString()));
    }
    return this;
  }

  /**
   * Filters a column if a string appears anywhere in that element.
   *
   * @param column the column to filer
   * @param values the allowed values
   * @return Instance of query builder used
   */
  public QueryBuilder<T> filterLike(String column, List<String> values) {
    if (values == null || values.isEmpty()) {
      // if there are no values it is best just to add a FALSE
      conditions.add("TRUE");
      return this;
    }
    String parameters = "?";
    conditionParams.add("%" + values.get(0) + "%");
    conditions.add("%s LIKE (%s)".formatted(column, parameters));

    for (int i = 1; i < values.size(); i++) {
      conditionParams.add("%" + values.get(i) + "%");
      conditions.add("OR AND %s LIKE (%s)".formatted(column, parameters));
    }
    return this;
  }

  /**
   * Filters a column - must be in set of allowed values.
   *
   * @param column Column to filter
   * @param values String of allowed values
   * @return Instance of query builder used
   */
  public QueryBuilder<T> filterIn(String column, List<String> values) {
    return filterIn(column, values, false);
  }

  /**
   * @param column The column to file
   * @param values Values to allow
   * @param knownValues All know value for the column, all the non-other values
   * @param other Should we allow the other values this time
   * @return Instance of query builder used
   */
  public QueryBuilder<T> filterInOrOther(
      String column, List<String> values, List<String> knownValues, boolean other) {
    // first just filter for the allowed values
    filterIn(column, values);

    if (other) {
      // filter for all values that are not in the known list aka 'other'
      filterIn(column, knownValues, true);

      // remove both filter conditions
      String filterNot = conditions.remove(conditions.size() - 1);
      String filter = conditions.remove(conditions.size() - 1);

      // put them back in as an or
      conditions.add("(%s OR %s)".formatted(filter, filterNot));
    }

    return this;
  }

  /**
   * Filter for one value across columns
   *
   * @param value The value to look for
   * @param columns The columns
   * @return Instance of query builder used
   */
  public QueryBuilder<T> filterInColumns(String value, List<String> columns) {
    if (columns == null || columns.isEmpty()) {
      // if there are no values it is best just to add a FALSE
      conditions.add("FALSE");
      return this;
    }

    // there will allways be at least one parameter
    StringBuilder inner = new StringBuilder("(" + columns.get(0) + " LIKE ?" + ")");
    conditionParams.add(value + "%");

    // if there are more than one, this for loop adds the remaining
    for (int i = 1; i < columns.size(); i++) {
      inner.append(" OR (").append(columns.get(i)).append(" LIKE ?").append(")");
      conditionParams.add(value + "%");
    }
    conditions.add("(" + inner + ")");

    return this;
  }

  /**
   * Get the SQL where statement constructed by all the filters.
   *
   * @return SQL for filters. Where statement.
   */
  private String getWhereString() {
    if (!conditions.isEmpty()) {
      String whereString = " WHERE " + String.join(" AND ", conditions);
      // look for OR usage and make it valid SQL
      whereString = whereString.replaceAll("AND OR AND", "OR");
      return whereString;
    } else {
      return "";
    }
  }

  /**
   * Get the SQL order by statement. Constructed using all the applied sorts.
   *
   * @return SQL for sorting. Order by statement.
   */
  private String getOrderByString() {
    if (!order.isEmpty()) {
      return " ORDER BY " + String.join(", ", order);
    } else {
      return "";
    }
  }

  /**
   * Constructs SQL to get a page of results with size pageSize. For example pageSize = 10,
   * pageNumber = 1, gets the * 11th to 20th results.
   *
   * @param pageNumber The page to select
   * @param pageSize The max number of results per page
   * @return SQL to select the page
   */
  public String getPageString(Integer pageNumber, Integer pageSize) {
    String builtQuery = "SELECT * FROM %s".formatted(dao.getTableName());

    builtQuery += getWhereString();
    builtQuery += getOrderByString();

    builtQuery += String.format(" LIMIT %d OFFSET %d", pageSize, pageSize * (pageNumber));

    return builtQuery + ";";
  }

  /**
   * Calculates the total number of pages based on the given page size.
   *
   * @param pageSize The number of items per page.
   * @return The total number of pages needed to display all items, rounded up.
   */
  public Integer getPageCount(Integer pageSize) {
    // make sure to convert to float or interger division
    return Math.max(1, (int) Math.ceil((float) getAllCount() / (float) pageSize));
  }

  /**
   * Gets SQL to select all the results with the filters and sorting.
   *
   * @return The SQL to select everything, with filters and sorting
   */
  public String getAllString() {
    String builtQuery = "SELECT * FROM %s".formatted(dao.getTableName());

    builtQuery += getWhereString();
    builtQuery += getOrderByString();

    return builtQuery + ";";
  }

  /**
   * Gets SQL to select a page but gives each result as a JSON object. Each JSON object only
   * includes the data specified with the columns parameter.
   *
   * @param columns The columns to include in the object.
   * @param pageNumber The page of results to get
   * @param pageSize The size of each page
   * @return SQL to select a page of results, where each result is a JSON object
   */
  public String getPageAsJSONString(List<String> columns, Integer pageNumber, Integer pageSize) {
    String jsonColumns =
        String.join(",", columns.stream().map(c -> String.format("'%s',%s", c, c)).toList());
    return getPageString(pageNumber, pageSize)
        .replace("SELECT *", String.format("SELECT JSON_OBJECT(%s) as json", jsonColumns));
  }

  /**
   * Get the params that will be passed in the SQL on its execution.
   *
   * @return List of SQL parameters.
   */
  public List<String> getParams() {
    return conditionParams;
  }

  /**
   * SQL to count the total number of results with given filters applied.
   *
   * @return The total number of results
   */
  public String getAllCountString() {
    String builtQuery = "SELECT COUNT(*) FROM %s".formatted(dao.getTableName());

    // only add the filters sorting would not change the number of results
    builtQuery += getWhereString();

    return builtQuery + ";";
  }

  /**
   * Clones the instance so filters/sorting can be applied without effecting the original instance
   *
   * @return New instance of the query with same filters and ordering applied.
   */
  @Override
  public QueryBuilder<T> clone() {
    QueryBuilder<T> cloned = new QueryBuilder<T>(dao) {};
    // make sure the lists are actually cloned
    cloned.conditions = new ArrayList<>(this.conditions);
    cloned.conditionParams = new ArrayList<>(this.conditionParams);
    cloned.order = new ArrayList<>(this.order);
    return cloned;
  }

  /**
   * Executes some SQL and returns all the results as instances of T
   *
   * @param sql The query to run
   * @return List of T instances for each result
   */
  private List<T> getResults(String sql) {
    List<String> params = getParams();
    List<T> results = new ArrayList<>();

    try (Connection connection = database.connect();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      for (int i = 0; i < params.size(); i++) {
        // SQL params are one-indexed - stupid
        statement.setString(i + 1, params.get(i));
      }
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        results.add(dao.getOneFromResult(resultSet));
      }
      return results;
    } catch (SQLException e) {
      log.error(e);
      return null;
    }
  }

  /**
   * Executes some SQL that gets each result as JSON.
   *
   * @param sql Query containing that gets some JSON objects
   * @return List of JSON Strings
   */
  private List<String> getResultsJSON(String sql) {
    List<String> params = getParams();
    List<String> results = new ArrayList<>();

    try (Connection connection = database.connect();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      for (int i = 0; i < params.size(); i++) {
        // SQL params are one-indexed - stupid
        statement.setString(i + 1, params.get(i));
      }
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        results.add(resultSet.getString("json"));
      }
      return results;
    } catch (SQLException e) {
      log.error(e);
      return null;
    }
  }

  /**
   * Executes SQL to get all results and returns the results as instances of T
   *
   * @see QueryBuilder#getAllString()
   * @return All the results
   */
  public List<T> getAll() {
    return getResults(getAllString());
  }

  /**
   * Gets just one result from query.
   *
   * @return The first result from the query
   */
  public T getFirst() {
    List<T> results = getPage(0, 1);
    if (results.isEmpty()) {
      return null;
    } else {
      return results.get(0);
    }
  }

  /**
   * Executes SQL to get a page of results and returns the results as instances of T
   *
   * @see QueryBuilder#getPageString(Integer, Integer)
   * @param pageNumber the page to get
   * @param pageSize size of each page
   * @return The results
   */
  public List<T> getPage(Integer pageNumber, Integer pageSize) {
    return getResults(getPageString(pageNumber, pageSize));
  }

  /**
   * Executes SQL to get a page of results as JSON and returns a List of JSON strings.
   *
   * @see QueryBuilder#getPageAsJSONString(List, Integer, Integer)
   * @param columns columns to include
   * @param pageNumber the page to get
   * @param pageSize the size of each page
   * @return The results
   */
  public List<String> getPageAsJSON(List<String> columns, Integer pageNumber, Integer pageSize) {
    return getResultsJSON(getPageAsJSONString(columns, pageNumber, pageSize));
  }

  /**
   * Executes SQL to get the count for all results.
   *
   * @see QueryBuilder#getAllCountString()
   * @return The number of results
   */
  public Integer getAllCount() {
    List<String> params = getParams();

    try (Connection connection = database.connect();
        PreparedStatement statement = connection.prepareStatement(getAllCountString())) {
      for (int i = 0; i < params.size(); i++) {
        statement.setString(i + 1, params.get(i));
      }

      ResultSet resultSet = statement.executeQuery();

      return resultSet.getInt(1);
    } catch (SQLException e) {
      log.error(e);
      return 0;
    }
  }

  /**
   * Clear all files and sorting.
   *
   * @return The QueryBuilder used
   */
  public QueryBuilder<T> clear() {
    conditions.clear();
    conditionParams.clear();
    order.clear();
    return this;
  }

  /**
   * Clear all sorting.
   *
   * @return The QueryBuilder used
   */
  public QueryBuilder<T> clearOrder() {
    order.clear();
    return this;
  }
}
