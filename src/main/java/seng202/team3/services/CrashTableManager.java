package seng202.team3.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import seng202.team3.dao.query.QueryBuilder;
import seng202.team3.models.Crash;
import seng202.team3.models.annotation.LabelUtils;

public class CrashTableManager {
  private CrashFilterManager crashFilterManager;
  private ObservableList<Crash> tableData = FXCollections.observableArrayList();
  private IntegerProperty pageNumber = new SimpleIntegerProperty(0);
  private IntegerProperty pageSize = new SimpleIntegerProperty(200);
  private IntegerProperty pageCount = new SimpleIntegerProperty(1);
  private IntegerProperty totalSize = new SimpleIntegerProperty(0);
  private ObservableList<Pair<String, Boolean>> sorts = FXCollections.observableArrayList();
  private ObjectProperty<QueryBuilder<Crash>> currentQuerySorted = new SimpleObjectProperty<>();
  private ObservableList<String> columns = FXCollections.observableArrayList(getInitialColumns());

  /**
   * Initializes crashFilterManager takes in the crashFilterManager to use as a base for the table
   * data.
   *
   * @param crashFilterManager incoming crash filter manager
   */
  public CrashTableManager(CrashFilterManager crashFilterManager) {
    this.crashFilterManager = crashFilterManager;
    this.currentQuerySorted.set(crashFilterManager.getCurrentQuery().getValue());
    crashFilterManager
        .getCurrentQuery()
        .addListener((observable, oldValue, newValue) -> updateCurrentQuerySorted());
    sorts.addListener(
        (ListChangeListener<Pair<String, Boolean>>) ((o) -> updateCurrentQuerySorted()));
    currentQuerySorted.addListener(
        (observable, oldValue, newValue) -> updateTableResultsAndCounts());
    pageSize.addListener(o -> updateTableResultsAndCounts());
    pageNumber.addListener(o -> updateTableResults());
    updateTableResultsAndCounts();
  }

  private void updateCurrentQuerySorted() {
    QueryBuilder<Crash> sortedQuery = crashFilterManager.getCurrentQuery().getValue().clone();
    for (Pair<String, Boolean> sort : sorts) {
      sortedQuery = sortedQuery.sort(sort.getKey(), sort.getValue());
    }
    currentQuerySorted.set(sortedQuery);
  }

  /**
   * Run when the table results and counts will be change the constructor for when these are setup.
   */
  private void updateTableResultsAndCounts() {
    pageCount.set(currentQuerySorted.getValue().getPageCount(pageSize.get()));
    totalSize.set(currentQuerySorted.getValue().getAllCount());
    updateTableResults();
  }

  /** Run when the table results need changing. Setup in constructor. */
  private void updateTableResults() {
    tableData.setAll(currentQuerySorted.getValue().getPage(pageNumber.get(), pageSize.get()));
  }

  /** Gets the observable table data */
  public ObservableList<Crash> getTableData() {
    return tableData;
  }

  /**
   * Changes the page
   *
   * @param newPageNumber the page to change to
   */
  public void setPageNumber(Integer newPageNumber) {
    pageNumber.setValue(newPageNumber);
  }

  /** Gets the current page */
  public Integer getPageNumber() {
    return pageNumber.getValue();
  }

  /**
   * Sets a new page size.
   *
   * @param newPageSize the new page size
   */
  public void setPageSize(Integer newPageSize) {
    pageSize.set(newPageSize);
  }

  /** Gets the current page size */
  public Integer getPageSize() {
    return pageSize.get();
  }

  /** Gets the total size of all results from the query. */
  public Integer getTotalSize() {
    return totalSize.get();
  }

  /** Gets the size of the data currently in the table. */
  public Integer getTableDataSize() {
    return tableData.size();
  }

  /**
   * Gets the range of rows currently in table data
   *
   * @return pair with the top and bottom row number
   */
  public Pair<Integer, Integer> getTableDataRows() {
    if (getTableDataSize() == 0) {
      return new Pair<Integer, Integer>(0, 0);
    }
    Integer start = getPageNumber() * getPageSize() + 1;
    Integer end = start + getTableDataSize() - 1;
    return new Pair<Integer, Integer>(start, end);
  }

  public ReadOnlyIntegerProperty getPageCountProperty() {
    return IntegerProperty.readOnlyIntegerProperty(pageCount);
  }

  public boolean onLastPage() {
    return pageNumber.get() == (pageCount.get() - 1);
  }

  public boolean onFirstPage() {
    return pageNumber.get() == 0;
  }

  /**
   * Apply a sort to the data. The attributes are related to the database columns not the table
   * column names.
   *
   * @param attributes the attributes given as a pair of attribute name and direction.
   */
  public void setSorts(List<Pair<String, Boolean>> attributes) {
    List<Pair<String, Boolean>> attributesCopy = new ArrayList<>(attributes);
    for (Pair<String, Boolean> attribute : attributesCopy) {
      if (attribute.getKey() == "location") {
        attributes.remove(attribute);
        attributes.add(new Pair<String, Boolean>("crashLocation1", attribute.getValue()));
        attributes.add(new Pair<String, Boolean>("crashLocation2", attribute.getValue()));
      }
    }
    sorts.setAll(attributes);
  }

  /**
   * Get all the possible table columns.
   *
   * @return The table column options
   */
  public List<String> getColumns() {
    List<String> columns = LabelUtils.getLabeledAttributes(Crash.class);
    columns.remove("id"); // don't show the id column on a table
    return columns;
  }

  /**
   * Gets the initial columns to show in the table
   *
   * @return The columns to show
   */
  public List<String> getInitialColumns() {
    return Arrays.asList("tlaName", "location", "crashYear", "crashSeverity", "bicycle");
  }

  /**
   * Gets the state of the columns observable
   *
   * @return the observable for the columns in the table
   */
  public ObservableList<String> getColumnsProperty() {
    return columns;
  }
}
