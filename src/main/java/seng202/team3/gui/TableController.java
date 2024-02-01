package seng202.team3.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckComboBox;
import seng202.team3.models.Crash;
import seng202.team3.models.annotation.LabelConverter;
import seng202.team3.models.annotation.LabelUtils;
import seng202.team3.services.CrashTableManager;

/**
 * Table controller class which currently displays and manages the table, table filters, pagination,
 * and page selection.
 *
 * @author Tom Gallagher (tga62)
 * @author Uday Daroch (uda12)
 * @author Charlie Porter (cpo57)
 * @author Hanan Fokkens (hfo22)
 * @author Tobias Paull (tpa122)
 * @author Ryan Scofield (rsc104)
 */
public class TableController extends Controller {
  private static final Logger log = LogManager.getLogger(TableController.class);
  /** TableView for the table page */
  @FXML private TableView<Crash> crashTableView = new TableView<>();

  @FXML private ComboBox<String> tableSizeComboBox;

  @FXML private CheckComboBox<String> columnCheckComboBox = new CheckComboBox<>();

  @FXML private Label resultsDescriptionLabel;
  @FXML private Pagination pagination;

  @FXML private GridPane tableMasterPane;
  @FXML private Button paginationJumpToEndButton;
  @FXML private Button paginationJumpToStartButton;

  private CrashTableManager crashTableManager;

  /**
   * this function takes in the string that is being selected by the user in the drop-down menu and
   * adds a Column to the table that is being displayed.
   *
   * @param attributeName the name of the selected attribute.
   */
  @FXML
  void addColumn(String attributeName) {
    // only add if the column doesn't exist
    if (crashTableView.getColumns().filtered(c -> c.getId().equals(attributeName)).isEmpty()) {
      // make a new column with the same name as the attribute
      TableColumn<Crash, ?> newColumn;
      try {
        newColumn = new TableColumn<>(LabelUtils.getLabel(Crash.class, attributeName));
      } catch (NoSuchFieldException e) {
        newColumn = new TableColumn<>(attributeName);
      }
      newColumn.setId(attributeName);

      newColumn.setSortable(true); // make it sortable

      // use a property factory to get the value for the column from the attribute
      newColumn.setCellValueFactory(new PropertyValueFactory<>(attributeName));

      // put it in the table view
      crashTableView.getColumns().add(newColumn);
    }
  }

  /**
   * Removes a column from the crashTableView based on the provided attribute name.
   *
   * <p>The method searches for a column with an ID matching the given attributeName and removes it
   * from the table view.
   *
   * @param attributeName The ID of the column to be removed from the table view.
   */
  @FXML
  void removeColumn(String attributeName) {
    // remove by the column text, this should match the attribute
    crashTableView.getColumns().removeIf(c -> c.getId().equals(attributeName));
  }

  /**
   * Initializes the pagination component, resetting to the first page on query changes and updating
   * page count based on the new query.
   */
  void initializePagination() {
    pagination.setPageFactory(
        new Callback<Integer, Node>() {
          @Override
          public VBox call(Integer page) {
            crashTableManager.setPageNumber(page);
            paginationJumpToEndButton.setDisable(crashTableManager.onLastPage());
            paginationJumpToStartButton.setDisable(crashTableManager.onFirstPage());
            return new VBox();
          }
        });
    pagination.pageCountProperty().bind(crashTableManager.getPageCountProperty());
    pagination.pageCountProperty().addListener(o -> pagination.setCurrentPageIndex(0));
  }

  /** User clicked the jump to end page button */
  @FXML
  public void paginationJumpToEnd() {
    pagination.setCurrentPageIndex(pagination.getPageCount());
  }

  /** User clicked the jump to start page button */
  @FXML
  public void paginationJumpToStart() {
    pagination.setCurrentPageIndex(0);
  }

  /** Resets the column selection to the default state */
  void resetColumnCheckComboBox() {
    // clear all columns
    columnCheckComboBox.getCheckModel().clearChecks();

    // now check the default initial columns
    for (String column : crashTableManager.getInitialColumns()) {
      columnCheckComboBox.getCheckModel().check(column);
    }
  }

  /**
   * Initializes the column check ComboBox with attributes from the Crash class, excluding the "id"
   * attribute. The ComboBox uses a label converter to present a user-friendly name for each
   * attribute. Listeners are added to the ComboBox's check model to handle the addition and removal
   * of columns based on user interactions.
   */
  void initializeColumnCheckComboBox() {
    columnCheckComboBox.getItems().setAll(crashTableManager.getColumns());

    // convert field name (from getColumns()) to nice formatted label
    columnCheckComboBox.setConverter(new LabelConverter(Crash.class));

    // crashTableManager.getColumnsProperty().bind(columnCheckComboBox.getCheckModel().getCheckedItems())
    columnCheckComboBox.getCheckModel().getCheckedItems();
    columnCheckComboBox
        .getCheckModel()
        .getCheckedItems()
        .addListener(
            (ListChangeListener<String>)
                ((c) -> {
                  while (c.next()) {
                    crashTableManager.getColumnsProperty().removeAll(c.getRemoved());
                    crashTableManager.getColumnsProperty().addAll(c.getAddedSubList());
                  }
                }));
    crashTableManager
        .getColumnsProperty()
        .addListener(
            (ListChangeListener<String>)
                ((c) -> {
                  while (c.next()) {
                    c.getRemoved().forEach(v -> removeColumn(v));
                    c.getAddedSubList().forEach(v -> addColumn(v));
                  }
                }));

    resetColumnCheckComboBox();
  }

  /** Initialized the table with appropriate listeners */
  private void initializeTableView() {
    // make it so that the table items come from the crash filtered observable
    crashTableView.setItems(crashTableManager.getTableData());
    crashTableView.setOnSort(
        (event) -> {
          event.consume();
          sortTable();
        });

    crashTableView.setOnMouseClicked(
        (MouseEvent event) -> {
          if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            Crash selectedCrash = crashTableView.getSelectionModel().getSelectedItem();
            if (selectedCrash != null) {
              log.info("Double clicked an entry, id: " + selectedCrash.getId());
              layoutController
                  .changeToMap()
                  .initialMapConfiguration(selectedCrash.getLat(), selectedCrash.getLng());
            }
          }
        });
  }

  /**
   * Initializes the table in table.fxml with the full data set
   *
   * @param layoutController The LayoutController to associate with this TableController.
   */
  @Override
  protected void initialize(LayoutController layoutController) {
    super.initialize(layoutController);
    crashTableManager = new CrashTableManager(layoutController.getCrashFilterManager());

    // first initialize all filter fields
    initializeColumnCheckComboBox();

    // initialize the pagination and table view now that the filters have been
    // applied
    initializePagination();
    initializeResultsDescriptionLabel();
    initializeTableView();
    initializeTableSizeComboBox();
    // initializeFilterCheckBox("weather");

    layoutController.applyFilters();
  }

  /**
   * Initial the table size combobox with observer that checks when editor is updated - if it is
   * then change the page count + re-query.
   */
  private void initializeTableSizeComboBox() {
    tableSizeComboBox.setItems(
        FXCollections.observableArrayList("5", "10", "25", "50", "100", "200", "500", "1000"));
    tableSizeComboBox.setEditable(true);
    tableSizeComboBox.setValue(crashTableManager.getPageSize().toString());

    tableSizeComboBox
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              try {
                crashTableManager.setPageSize(Integer.valueOf(newValue));
                tableSizeComboBox.setStyle("");
              } catch (NumberFormatException e) {
                tableSizeComboBox.setStyle("-fx-background-color: #f66767;");
              }
            });
  }

  /**
   * Updates the unique page elements related to the table page. This Adds the column CheckComboBox.
   */
  @Override
  public void pageChanged() {
    Label labelTable = new Label("Table");
    labelTable.getStyleClass().add("header");

    Pane paneHeader = new Pane();
    paneHeader.setStyle("-fx-background-color: black;");
    paneHeader.setPrefSize(200.0, 1.0);

    Label filterHeader = new Label("Displayed Columns");
    filterHeader.getStyleClass().add("filter-header");

    layoutController.vboxPageUnique.getChildren().clear();
    layoutController
        .vboxPageUnique
        .getChildren()
        .addAll(labelTable, paneHeader, filterHeader, columnCheckComboBox);
  }

  private String getResultsDescriptionLabel() {
    Pair<Integer, Integer> tableDataRows = crashTableManager.getTableDataRows();
    Integer totalSize = crashTableManager.getTotalSize();

    if (totalSize == 0) {
      return "Showing no results";
    } else {
      return "Showing results %d to %d out of %d"
          .formatted(tableDataRows.getKey(), tableDataRows.getValue(), totalSize);
    }
  }

  private void initializeResultsDescriptionLabel() {
    crashTableManager
        .getTableData()
        .addListener(
            (ListChangeListener<Crash>)
                ((e) -> resultsDescriptionLabel.setText(getResultsDescriptionLabel())));
  }

  /** Apply sort to the query based on the column that the user has selected */
  public void sortTable() {
    List<Pair<String, Boolean>> sorts = new ArrayList<>();
    for (TableColumn<Crash, ?> col : crashTableView.getSortOrder()) {
      Boolean isReversed = col.getSortType().toString().equals("DESCENDING");
      sorts.add(new Pair<String, Boolean>(col.getId(), isReversed));
    }
    crashTableManager.setSorts(sorts);
  }
}
