package seng202.team3.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import seng202.team3.dao.query.QueryBuilder;

/**
 * Interface for Database Access Objects (DAOs) that provides common functionality for database
 * access
 *
 * @author Morgan English
 * @author Hanan Fokkens
 */
public interface DAOInterface<T> {
  /**
   * Adds a single object of type T to database
   *
   * @param toAdd object of type T to add
   * @return the id of the inserted data or null for bad insert
   */
  Integer add(T toAdd);

  /**
   * Deletes an object in the database
   *
   * @param toDelete Object that needs deleting
   * @return true if success, false for error/fail
   */
  boolean delete(T toDelete);

  /**
   * Updates an object in the database
   *
   * @param toUpdate Object that needs to be updated (this object must be able to identify itself
   *     and its previous self)
   */
  void update(T toUpdate);

  /**
   * Create one object of T from a result set
   *
   * @param resultSet the result set
   * @return The new object
   * @throws SQLException If a SQL exception occurs
   */
  public T getOneFromResult(ResultSet resultSet) throws SQLException;

  /**
   * Used to get all items from the database.
   *
   * @return List of all items.
   */
  public List<T> getAll();

  /**
   * Used to get items from the database
   *
   * @return DAOFilter that is used to construct a query.
   */
  public QueryBuilder<T> query();

  /**
   * Used to get name of table from database.
   *
   * @return Name of table.
   */
  public String getTableName();
}
