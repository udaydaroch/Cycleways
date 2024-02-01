package seng202.team3.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;
import seng202.team3.services.CrashFilterManager;

/**
 * Layout controller manages the transitions
 *
 * @author Hanan Fokkens
 * @author Tobias Paull
 */
public class LayoutController {
  private static final Logger log = LogManager.getLogger(LayoutController.class);

  private final CrashFilterManager crashFilterManager = new CrashFilterManager();

  private static String current = null;
  private final Duration toolTipShowDelayInstant = new Duration(0);
  private static final HashMap<String, Pair<Parent, Controller>> roots = new HashMap<>();

  @FXML Label vehicleInfo;

  @FXML BorderPane borderPane;

  @FXML private Button mapButton;

  @FXML private Button tableButton;
  @FXML private GridPane tableMasterPane;

  @FXML private VBox vboxFilters;
  /** Vbox at the top of the left pane to include the elements unique to table and map page. */
  @FXML public VBox vboxPageUnique;

  @FXML private RangeSlider yearSlider;
  @FXML private RangeSlider speedLimitSlider;
  @FXML private Label sliderYearLow = new Label();
  @FXML private Label sliderYearHigh = new Label();
  @FXML private Label sliderSpeedLow = new Label();
  @FXML private Label sliderSpeedHigh = new Label();
  @FXML private TextField roadSearchField;
  @FXML private TextField regionSearchField;

  @FXML private Button vehicleMin;
  @FXML private CheckBox bicycle_Checkbox;
  @FXML private CheckBox moped_Checkbox;

  @FXML private CheckBox motorcycle_Checkbox;

  @FXML private CheckBox car_Checkbox;
  @FXML private CheckBox taxi_Checkbox;

  @FXML private CheckBox suv_Checkbox;

  @FXML private CheckBox van_Checkbox;

  @FXML private CheckBox bus_Checkbox;
  @FXML private CheckBox schoolBus_Checkbox;

  @FXML private CheckBox truck_Checkbox;
  @FXML private CheckBox other_Checkbox;

  @FXML private CheckBox bikeOnlyCheckBox;
  @FXML private Button applyBtn;
  @FXML private TextField roadSearchTextField;

  @FXML private ScrollPane filterPane;
  @FXML private Button toggleFilterButton;

  private static final String selectedButtonStyleClass = "selected-button";

  /** Load in a parent using cache */
  private Pair<Parent, Controller> load(String scene) {
    if (roots.containsKey(scene)) {
      Pair<Parent, Controller> root = roots.get(scene);
      root.getValue().pageChanged();
      return root;
    }

    FXMLLoader layoutLoader =
        new FXMLLoader(getClass().getResource("/fxml/%s.fxml".formatted(scene)));

    try {
      Parent rootParent = layoutLoader.load();
      Controller rootController = layoutLoader.getController();
      rootController.initialize(this);
      roots.put(scene, new Pair<>(rootParent, rootController));
    } catch (IOException e) {
      log.error("Here");
      e.printStackTrace();
    }

    return roots.get(scene);
  }

  /**
   * Attempts to pre-loads a scene
   *
   * @param scene the scene to load
   */
  public void preLoad(String scene) {
    try {
      load(scene);
    } catch (Exception e) {
      e.getStackTrace();
    }
  }

  /**
   * Switches to one of the three primary pages: map, table, or graph. If the requested page is
   * already the current one, no changes are made.
   *
   * @param scene The name or identifier of the page (either "map", or "table") to switch to.
   * @return The controller associated with the loaded scene.
   */
  public Controller changeTo(String scene) {
    Pair<Parent, Controller> root = load(scene);
    if (current != null && current.equals(scene)) {
      // if we are already on the scene
      // lets get out of here!
      return root.getValue();
    }

    borderPane.centerProperty().setValue(root.getKey());
    updateButtonStyles(scene);

    // make sure we track where we're at
    current = scene;
    return root.getValue();
  }

  /**
   * Switches to the map page.
   *
   * @return The MapController associated with the map scene.
   */
  @FXML
  public MapController changeToMap() {
    return (MapController) changeTo("map");
  }

  /**
   * Switches to the table page.
   *
   * @return The TableController associated with the Table scene.
   */
  @FXML
  public TableController changeToTable() {
    return (TableController) changeTo("table");
  }

  /**
   * Updates the style of the scene buttons based on the currently active scene. The button
   * corresponding to the active scene gets a "selected-button" style.
   *
   * @param currentScene The name of the currently active scene (either "graph", "map", or "table").
   */
  private void updateButtonStyles(String currentScene) {
    mapButton.getStyleClass().remove(selectedButtonStyleClass);
    tableButton.getStyleClass().remove(selectedButtonStyleClass);

    switch (currentScene) {
      case "map" -> mapButton.getStyleClass().add(selectedButtonStyleClass);
      case "table" -> tableButton.getStyleClass().add(selectedButtonStyleClass);
    }
  }

  private List<String> getAttributeArray(String attribute) {
    return switch (attribute) {
      case "crashSeverity" -> crashFilterManager.getSeverityValues();
      case "region" -> crashFilterManager.getRegionValues();
      case "weather" -> new ArrayList<>(
          Arrays.asList(
              "Fine",
              "Light Rain",
              "Heavy Rain",
              "Mist or Fog",
              "Snow",
              "Hail or Sleet",
              "Strong Wind",
              "Frost"));
      default -> List.of();
    };
  }

  void resetYearSlider() {
    // ensure that the thumbs are at the ends of the slider
    yearSlider.setHighValue(crashFilterManager.getYearMaxValue());
    yearSlider.setLowValue(crashFilterManager.getYearMinValue());
  }

  void initializeYearSlider() {
    yearSlider.setMax(crashFilterManager.getYearMaxValue());
    yearSlider.setMin(crashFilterManager.getYearMinValue());

    yearSlider
        .lowValueProperty()
        .addListener(
            (observable, oldValue, newValue) ->
                sliderYearLow.textProperty().setValue(String.valueOf(newValue.intValue())));

    yearSlider
        .highValueProperty()
        .addListener(
            (observable, oldValue, newValue) ->
                sliderYearHigh.textProperty().setValue(String.valueOf(newValue.intValue())));

    resetYearSlider();
  }

  private void initializeFilterCheckBox(String attribute, String name) {
    List<String> attributesArray = getAttributeArray(attribute);

    Pane paneHeader = new Pane();
    paneHeader.setStyle("-fx-background-color: black;");
    paneHeader.setPrefSize(200.0, 1.0);

    Label filterHeader;
    if (name.isEmpty()) {
      filterHeader = new Label(WordUtils.capitalize(attribute));
      name = attribute;
    } else {
      filterHeader = new Label(name);
    }
    Tooltip anyFilterTooltip =
        new Tooltip(
            "Crashes with ANY of the selected values for '"
                + WordUtils.capitalizeFully(name)
                + "' will be included");
    anyFilterTooltip.setShowDelay(toolTipShowDelayInstant);
    Label info = new Label("?");
    info.setTooltip(anyFilterTooltip);
    Shape circle = new Circle(7.5);

    circle.getStyleClass().add("info-circle");
    info.getStyleClass().add("info-header");
    filterHeader.getStyleClass().add("filter-header");
    AnchorPane.setLeftAnchor(filterHeader, 0.0);
    AnchorPane.setLeftAnchor(info, 66.4);
    AnchorPane.setTopAnchor(info, 12.55);
    AnchorPane.setLeftAnchor(circle, 62.0);
    AnchorPane.setTopAnchor(circle, 12.75);

    Button minButton = new Button("-");
    String unstrippedMinID = attribute + "ID";
    String minButtonID = unstrippedMinID.replaceAll("\\s", "");
    minButton.setId(minButtonID);
    minButton.setOnAction(event -> toggleCheckBoxesVisibilityAndButtonText(attribute));
    minButton.getStyleClass().add("min-button");
    AnchorPane.setRightAnchor(minButton, 0.0);

    AnchorPane filterAnchor = new AnchorPane(filterHeader, circle, info, minButton);

    vboxFilters.getChildren().addAll(paneHeader, filterAnchor);

    for (String s : attributesArray) {
      CheckBox newBox = new CheckBox(WordUtils.capitalize(s));
      String unstrippedID = s + "ID";
      String checkboxID = unstrippedID.replaceAll("\\s", "");
      newBox.setId(checkboxID);
      newBox.setStyle("-fx-padding: 0 0 5 0px;");

      vboxFilters.getChildren().add(newBox);

      if (attribute.equals("crashSeverity")) {
        switch (s) {
          case "0" -> newBox.setText("Non Injury");
          case "1" -> newBox.setText("Minor Injury");
          case "2" -> newBox.setText("Serious Injury");
          case "3" -> newBox.setText("Fatal Crash");
          default -> newBox.setText("Null");
        }
      }
    }
    Pane invisiblePane = new Pane();
    invisiblePane.setPrefHeight(5.0);
    vboxFilters.getChildren().add(invisiblePane);
  }

  private void toggleVehicleVisibilityAndButtonText() {
    if ("-".equals(vehicleMin.getText())) {
      vehicleMin.setText("+");
      setVehicleVisibilityAndManaged(false);
    } else {
      vehicleMin.setText("-");
      setVehicleVisibilityAndManaged(true);
    }
  }

  private void setVehicleVisibilityAndManaged(boolean state) {
    bicycle_Checkbox.setVisible(state);
    moped_Checkbox.setVisible(state);
    motorcycle_Checkbox.setVisible(state);
    car_Checkbox.setVisible(state);
    taxi_Checkbox.setVisible(state);
    suv_Checkbox.setVisible(state);
    van_Checkbox.setVisible(state);
    truck_Checkbox.setVisible(state);
    bus_Checkbox.setVisible(state);
    schoolBus_Checkbox.setVisible(state);
    other_Checkbox.setVisible(state);

    bicycle_Checkbox.setManaged(state);
    moped_Checkbox.setManaged(state);
    motorcycle_Checkbox.setManaged(state);
    car_Checkbox.setManaged(state);
    taxi_Checkbox.setManaged(state);
    suv_Checkbox.setManaged(state);
    van_Checkbox.setManaged(state);
    truck_Checkbox.setManaged(state);
    bus_Checkbox.setManaged(state);
    schoolBus_Checkbox.setManaged(state);
    other_Checkbox.setManaged(state);
  }

  private void toggleCheckBoxesVisibilityAndButtonText(String attribute) {
    String unstrippedID = attribute + "ID";
    String minButtonID = unstrippedID.replaceAll("\\s", "");
    Button minButton = (Button) vboxFilters.lookup("#" + minButtonID);

    if ("-".equals(minButton.getText())) {
      minButton.setText("+");
      setCheckBoxesVisibilityAndManaged(false, attribute);
    } else {
      minButton.setText("-");
      setCheckBoxesVisibilityAndManaged(true, attribute);
    }
  }

  private void setCheckBoxesVisibilityAndManaged(boolean state, String attribute) {
    List<String> attributeArray = getAttributeArray(attribute);

    for (String s : attributeArray) {
      String unstrippedID = s + "ID";
      String checkboxID = unstrippedID.replaceAll("\\s", "");
      CheckBox currentCheckBox = (CheckBox) vboxFilters.lookup("#" + checkboxID);
      currentCheckBox.setVisible(state);
      currentCheckBox.setManaged(state);
    }
  }

  /** Applies all filters if the fields are not blank */
  @FXML
  public void applyFilters() {
    crashFilterManager.setNextYearFilter(yearSlider.getLowValue(), yearSlider.getHighValue());
    crashFilterManager.setNextSpeedFilter(
        speedLimitSlider.getLowValue(), speedLimitSlider.getHighValue());
    crashFilterManager.setNextSelectedRegions(getCheckedFilters("region"));
    crashFilterManager.setNextSelectedSeverity(getCheckedFilters("crashSeverity"));
    crashFilterManager.setNextSelectedWeather(getCheckedFilters("weather"));
    crashFilterManager.setNextRoadFilter(roadSearchTextField.getText());
    crashFilterManager.setNextVehiclesInvolvedFilter(getCheckedVehicles());
    crashFilterManager.setNextOnlyBicycleCrashesFilter(bikeOnlyCheckBox.isSelected());

    crashFilterManager.applyNextFilters();
  }

  /**
   * updates the list of checked vehicle types and returns it
   *
   * @return the list of vehicle types selected to filter by
   */
  public List<String> getCheckedVehicles() {
    List<String> checked = new ArrayList<>();
    if (moped_Checkbox.isSelected()) {
      checked.add("moped");
    }
    if (motorcycle_Checkbox.isSelected()) {
      checked.add("motorcycle");
    }
    if (car_Checkbox.isSelected()) {
      checked.add("carStationWagon");
    }
    if (taxi_Checkbox.isSelected()) {
      checked.add("taxi");
    }
    if (bus_Checkbox.isSelected()) {
      checked.add("bus");
    }
    if (schoolBus_Checkbox.isSelected()) {
      checked.add("schoolBus");
    }
    if (suv_Checkbox.isSelected()) {
      checked.add("SUV");
    }
    if (van_Checkbox.isSelected()) {
      checked.add("vanOrUtility");
    }
    if (truck_Checkbox.isSelected()) {
      checked.add("truck");
    }

    if (other_Checkbox.isSelected()) {
      checked.add("otherVehicleType");
    }
    return checked;
  }

  /**
   * gets a list of all options selected from the checkboxes for a given attriibute
   *
   * @param attribute the corresponding column in the database
   * @return list of all checked options
   */
  public List<String> getCheckedFilters(String attribute) {
    List<String> attributeArray = getAttributeArray(attribute);
    List<String> selectedFilters = new ArrayList<>();

    for (String s : attributeArray) {
      String unstrippedID = s + "ID";
      String checkboxID = unstrippedID.replaceAll("\\s", "");
      CheckBox currentCheckBox = (CheckBox) vboxFilters.lookup("#" + checkboxID);
      if (currentCheckBox.isSelected()) {
        selectedFilters.add(s);
      }
    }

    return selectedFilters;
  }

  /** Initialize the layout controller */
  @FXML
  void initialize() {
    // first initialize all filter fields
    initializeYearSlider();
    initializeSpeedSlider();
    vehicleMin.setOnAction(event -> toggleVehicleVisibilityAndButtonText());

    initializeFilterCheckBox("region", "");

    initializeFilterCheckBox("crashSeverity", "Severity");
    initializeFilterCheckBox("weather", "Weather");
    resetFilters();
    initializeToggleFilterButton();
    changeTo("table");

    initialiseVehicleToolTip();
  }

  /** sets all filter attributes to their initial state */
  public void resetFilters() {
    resetYearSlider();
    resetSpeedSlider();
    bikeOnlyCheckBox.setSelected(false);
    roadSearchTextField.setText("");
    bicycle_Checkbox.setSelected(false);
    moped_Checkbox.setSelected(false);
    motorcycle_Checkbox.setSelected(false);
    bus_Checkbox.setSelected(false);
    schoolBus_Checkbox.setSelected(false);
    car_Checkbox.setSelected(false);
    car_Checkbox.setSelected(false);
    suv_Checkbox.setSelected(false);
    van_Checkbox.setSelected(false);
    truck_Checkbox.setSelected(false);
    other_Checkbox.setSelected(false);
  }
  /**
   * called when reset filters button is pressed sets all filter attributes to their initial state
   * and applies
   */
  @FXML
  public void resetButtonPressed() {
    resetFilters();
    applyFilters();
  }

  /** Resets the speed limit slider to its default value */
  void resetSpeedSlider() {
    // ensure that the thumbs are at the ends of the slider
    speedLimitSlider.setLowValue(crashFilterManager.getSpeedMinValue());
    speedLimitSlider.setHighValue(crashFilterManager.getSpeedMaxValue());
  }

  /** Initialize the speed limit slider with appropriate listeners */
  void initializeSpeedSlider() {
    speedLimitSlider.setMax(crashFilterManager.getSpeedMaxValue());
    speedLimitSlider.setMin(crashFilterManager.getSpeedMinValue());
    speedLimitSlider
        .lowValueProperty()
        .addListener(
            (observable, oldValue, newValue) ->
                sliderSpeedLow.textProperty().setValue(String.valueOf(newValue.intValue())));
    speedLimitSlider
        .highValueProperty()
        .addListener(
            (observable, oldValue, newValue) ->
                sliderSpeedHigh.textProperty().setValue(String.valueOf(newValue.intValue())));
    resetSpeedSlider();
  }

  private void initializeToggleFilterButton() {
    toggleFilterButton.setText("<");
  }

  /** Toggles the visibility of the filter pane and updates the toggle button text. */
  @FXML
  public void toggleFilterPane() {
    filterPane.setManaged(!filterPane.isVisible());
    filterPane.setVisible(!filterPane.isVisible());

    if (filterPane.isVisible()) {
      toggleFilterButton.setText("<");
    } else {
      toggleFilterButton.setText(">");
    }
  }

  /**
   * Returns the CrashFilterManager.
   *
   * @return The CrashFilterManager instance.
   */
  public CrashFilterManager getCrashFilterManager() {
    return crashFilterManager;
  }

  /** Initialises the tool tips for vehicles involved filter. */
  public void initialiseVehicleToolTip() {
    Tooltip vehicleTip =
        new Tooltip("Only crashes including ALL checked vehicle types will be included");
    vehicleTip.setShowDelay(toolTipShowDelayInstant);
    vehicleInfo.setTooltip(vehicleTip);
  }
}
