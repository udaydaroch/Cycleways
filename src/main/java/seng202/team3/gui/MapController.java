package seng202.team3.gui;

// import com.sun.javafx.webkit.WebConsoleListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team3.dao.SavedRouteDAO;
import seng202.team3.dao.query.QueryBuilder;
import seng202.team3.models.Crash;
import seng202.team3.models.Position;
import seng202.team3.models.Route;
import seng202.team3.models.SavedRoute;
import seng202.team3.services.AddressPredictor;
import seng202.team3.services.JavaScriptBridge;
import seng202.team3.services.NetworkUtils;
import seng202.team3.services.RouteSegementBox;

/**
 * Map controller class which currently displays and manages the map, map filters, address go-to,
 * journey planning and journey saving.
 *
 * @author Tom Gallagher (tga62)
 * @author Uday Daroch (uda12)
 * @author Charlie Porter (cpo57)
 * @author Hanan Fokkens (hfo22)
 * @author Tobias Paull (tpa122)
 * @author Ryan Scofield (rsc104)
 */
public class MapController extends Controller {

  private static final Logger log = LogManager.getLogger(MapController.class);
  private ArrayList<HashMap<List<Position>, List<Position>>> boxesPerRoute = new ArrayList<>();
  @FXML private ComboBox<String> startComboBox;
  @FXML private ComboBox<String> destinationComboBox;
  @FXML private ComboBox<String> goToComboBox;

  @FXML private VBox mapParentBox;
  @FXML private WebView webView;
  @FXML private Pane noInternetPane;

  private Boolean pointsOn = false;
  private Boolean heatMapOn = false;
  private WebEngine webEngine;
  private JavaScriptBridge javaScriptBridge;
  private JSObject javaScriptConnector;

  public List<Route> alternativeRoutes = new ArrayList<>();
  private HashMap<List<Position>, List<Position>> boxes = new HashMap<>();
  @FXML private ComboBox<SavedRoute> routeComboBox;
  private final SavedRouteDAO savedRouteDAO = new SavedRouteDAO();
  /** stores whether the map has been intialised */
  private boolean mapExists = false;

  /**
   * Initializes the view when the controller is created. This method is automatically called after
   * the FXML file has been loaded. It calls the {@code setupView} function to configure the initial
   * state of the view components.
   *
   * @param layoutController The LayoutController to associate with this MapController.
   */
  @Override
  public void initialize(LayoutController layoutController) {
    super.initialize(layoutController);
    mapExists = true;
    initializeWebView();
    AddressPredictor.setupAddressPredictorComboBox(destinationComboBox);
    AddressPredictor.setupAddressPredictorComboBox(startComboBox);
    AddressPredictor.setupAddressPredictorComboBox(goToComboBox);
    initializeRoutes();
    layoutController
        .getCrashFilterManager()
        .getCurrentQuery()
        .addListener((observable, oldValue, newValue) -> setCrashesFromList(newValue));
  }

  /** @return the current status of pointsOn. */
  public Boolean getPointsOn() {
    return pointsOn;
  }

  /** Sets the status of pointsOn. */
  public void setPointsOn(Boolean pointsOn) {
    this.pointsOn = pointsOn;
  }

  /** @return the current status of heatMapOn. */
  public Boolean getHeatMapOn() {
    return heatMapOn;
  }

  /** Sets the status of heatMapOn. */
  public void setHeatMapOn(Boolean heatMapOn) {
    this.heatMapOn = heatMapOn;
  }


  /** Handles loss of internet, shows error page to user. */
  public void handleNoInternet() {
    mapParentBox.getChildren().remove(webView);
    mapParentBox.getChildren().remove(noInternetPane);
    mapParentBox.getChildren().add(noInternetPane);
  }

  /** Re-loads the map page in the web-view */
  public void resetWebView() {

    if (!NetworkUtils.isInternetAvailable()) {
      handleNoInternet();
      return;
    }

    mapParentBox.getChildren().remove(webView);
    mapParentBox.getChildren().remove(noInternetPane);
    mapParentBox.getChildren().add(webView);
    URL mapHtmlURL = getClass().getResource("/html/map.html");

    if (mapHtmlURL != null) {
      webEngine.load(mapHtmlURL.toExternalForm());
    } else {
      log.error("Failed to load map.html. Resource not found.");
      handleNoInternet();
    }
  }

  /**
   * Initialises the WebView loading in the appropriate html and initialising important communicator
   * objects between Java and Javascript
   */
  public void initializeWebView() {

    webEngine = webView.getEngine();
    webEngine.setJavaScriptEnabled(true);

    webEngine
        .getLoadWorker()
        .stateProperty()
        .addListener(
            (ov, oldState, newState) -> {
              // if our page has been loaded and the javascript has started
              if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");

                javaScriptBridge = new JavaScriptBridge(this::addCrashCard);
                window.setMember("javaScriptBridge", javaScriptBridge);

                webEngine.executeScript(
                    "console.log = (message) => { javaScriptBridge.consoleLog(message); };");

                // get a reference to the js object that has a reference to the js methods we need
                // to use in java
                this.javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                // call the javascript function to initialise the map
                javaScriptConnector.call("initializeMap");
                Platform.runLater(() -> layoutController.applyFilters());
              }
            });

    resetWebView();
  }

  /**
   * Initializes the map view controls in the UI.
   *
   * <p>Constructs UI elements for toggling between 'Points' and 'Heatmap' views on a map.
   * Configures and styles toggle buttons, linking them to their action events. Inserts the UI
   * elements into 'vboxPageUnique', replacing its current content.
   */
  @Override
  public void pageChanged() {

    Label labelMapView = new Label("Map View");
    labelMapView.getStyleClass().add("header");

    Pane paneHeader = new Pane();
    paneHeader.setStyle("-fx-background-color: black;");
    paneHeader.setPrefSize(200.0, 1.0);

    Pane invisiblePane = new Pane();
    invisiblePane.setPrefHeight(10.0);

    HBox hBoxPointsToggle = new HBox();

    String cssPath = "/css/map.css";
    URL cssURL = getClass().getResource(cssPath);
    String cssUrl = cssURL.toExternalForm();

    ToggleGroup toggleGroupPoints = new ToggleGroup();
    ToggleButton toggleButtonPoints = new ToggleButton("Points");
    toggleButtonPoints.setPrefWidth(100);
    toggleButtonPoints.setId("toggleButtonPoints");
    toggleButtonPoints.getStylesheets().add(cssUrl);
    toggleButtonPoints.setOnAction(event -> togglePoints());
    toggleButtonPoints.setToggleGroup(toggleGroupPoints);

    ToggleButton toggleButtonHeatmap = new ToggleButton("Heatmap");
    toggleButtonHeatmap.setPrefWidth(100);
    toggleButtonHeatmap.setId("toggleButtonHeatmap");
    toggleButtonHeatmap.getStylesheets().add(cssUrl);
    toggleButtonHeatmap.setOnAction(event -> toggleHeatmap());
    toggleButtonHeatmap.setToggleGroup(toggleGroupPoints);

    initMapButtons(toggleButtonPoints, toggleButtonHeatmap);

    hBoxPointsToggle.getChildren().addAll(toggleButtonPoints, toggleButtonHeatmap);

    layoutController.vboxPageUnique.getChildren().clear();
    layoutController
            .vboxPageUnique
            .getChildren()
            .addAll(labelMapView, paneHeader, invisiblePane, hBoxPointsToggle);
  }

  /** Initializes map toggle buttons' selection based on their state. */
  private void initMapButtons(ToggleButton pointsButton, ToggleButton heatmapButton) {
    pointsButton.setSelected(getPointsOn());
    heatmapButton.setSelected(getHeatMapOn());
  }


  /**
   * Handles the action when the refresh button is clicked. Calls the {@code setupView} function to
   * reset and configure the view components to their default state.
   *
   * @param event The action event triggered by the button click.
   */
  @FXML
  public void handleRefreshButtonAction(ActionEvent event) {
    resetWebView();
  }

  /**
   * Initializes the routes in the routeComboBox.
   *
   * <p>This method fetches all the saved routes from the persistent storage and populates the
   * routeComboBox with these routes. Before adding the new routes, it clears any existing items in
   * the ComboBox.
   */
  private void initializeRoutes() {
    try {
      List<SavedRoute> routes = savedRouteDAO.getAll();
      routeComboBox.getItems().clear(); // Clear existing items before adding new ones

      for (SavedRoute route : routes) {
        routeComboBox.getItems().add(route);
      }
      log.info("Routes initialized successfully.");
    } catch (Exception e) {
      log.error("Error initializing routes: ", e);
    }
  }

  /**
   * Checks if a route with the specified start and end locations already exists.
   *
   * @param start The start location of the route to check.
   * @param end The end location of the route to check.
   * @return true if the route does not exist, false otherwise or if an error occurs.
   */
  @FXML
  private boolean checkNotSaved(String start, String end) {
    try {
      for (SavedRoute route : routeComboBox.getItems()) {
        if (route.getStartLocation().equals(start) && route.getEndLocation().equals(end)) {
          return false;
        }
      }
      return true;
    } catch (Exception e) {
      log.error("Error checking if route is saved: ", e);
      return false;
    }
  }

  /**
   * Adds a new route from user-inputted start and end locations, after validations. Validates for
   * non-empty fields, location validity, route uniqueness, and storage limit. Shows warnings for
   * validation failures and errors during saving.
   *
   * @throws SQLException if an error occurs during route saving in the database.
   */
  @FXML
  private void addSavedRoute() {
    String start = startComboBox.getEditor().getText();
    String end = destinationComboBox.getEditor().getText();

    if (start == null || start.trim().isEmpty() || end == null || end.trim().isEmpty()) {
      showWarning("Both or one of the addresses are empty. Please provide valid addresses.");
      return;
    }

    if (!verifyRoute(start, end)) { // show warning done in here due to many cases
      return;
    }

    if (!checkNotSaved(start, end)) {
      showWarning("Route is Already Saved.");
      return;
    }

    if (savedRouteDAO.query().getAllCount() >= 10) {
      showWarning(
          "Maximum limit of 10 routes reached. Please remove an existing route before adding a new"
              + " one.");
      return;
    }

    try {
      savedRouteDAO.add(new SavedRoute(start, end));
      initializeRoutes();
      log.info("Route added successfully: Start - " + start + ", End - " + end);
    } catch (Exception e) {
      log.error("Error adding route: Start - " + start + ", End - " + end, e);
      showWarning("Failed to add route due to an error.");
    }
  }

  /**
   * Verifies the validity of the provided start and end addresses by querying their geolocation. If
   * either the start or end address is invalid, a warning is shown to the user.
   *
   * @param start The start address to be verified.
   * @param end The end address to be verified.
   * @return {@code true} if both start and end addresses are valid; {@code false} otherwise.
   */
  private boolean verifyRoute(String start, String end) {

    if (start == null || start.isEmpty() || end == null || end.isEmpty()) {
      showWarning("Both or one of the addresses are empty. Please provide valid addresses.");
      return false;
    }

    Position startPosition = AddressPredictor.getPositionFromAddress(start);
    Position endPosition = AddressPredictor.getPositionFromAddress(end);

    if (startPosition == null || endPosition == null) {
      showWarning(
          "Could not determine the position for one or both of the addresses. Please check the"
              + " addresses and try again.");
      return false;
    }

    if (startPosition.getLat() != 0d
        && startPosition.getLng() != 0d
        && endPosition.getLat() != 0d
        && endPosition.getLng() != 0d) {
      return true;
    } else {
      if (startPosition.getLat() == 0d
          && startPosition.getLng() == 0d
          && endPosition.getLat() == 0d
          && endPosition.getLng() == 0d) {
        showWarning("Both start and end addresses are invalid. Please check and try again.");
      } else if (startPosition.getLat() == 0d && startPosition.getLng() == 0d) {
        showWarning("The start address is invalid. Please check and try again.");
      } else {
        showWarning("The end address is invalid. Please check and try again.");
      }
      return false;
    }
  }

  /**
   * Handles the removal of a saved route from the routeComboBox.
   *
   * <p>If a route is selected from the ComboBox, this method attempts to remove the selected route
   * from the persistent storage. After removal, it re-initializes the routes in the ComboBox. If no
   * route is selected, a warning is logged.
   */
  @FXML
  private void removeSavedRoute() {
    SavedRoute selectedRoute = routeComboBox.getSelectionModel().getSelectedItem();
    if (selectedRoute != null) {
      if (savedRouteDAO.delete(selectedRoute)) {
        initializeRoutes();
        log.info("Route removed successfully: " + selectedRoute);
      } else {
        log.error("Error removing route: " + selectedRoute);
        showWarning("Failed to remove the selected route due to an error.");
      }
    } else {
      log.warn("No route selected for removal.");
      showWarning("Please select a route to remove.");
    }
  }
  /**
   * Displays a route on the WebView map using the underlying js command
   *
   * @param newRoute route to be displayed, made up of 2 or more Positions
   * @param routeRating rating that will determine the color of the route;
   */
  private void displayRouteOnMap(Route newRoute, Integer routeId, Integer routeRating) {
    String routeColor = "";

    switch (routeRating) {
      case 3:
        routeColor = "blue";
        break;
      case 2:
        routeColor = "green";
        break;
      case 1:
        routeColor = "purple";
        break;
    }
    javaScriptConnector.call("displayRoute", newRoute.toJSONArray(), routeColor, routeId);
  }

  /** Removes the route from the WebView map (if currently shown) */
  @FXML
  public void removeRouteFromMap() {
    javaScriptConnector.call("closeCrashCard");
    javaScriptConnector.call("removeRoute");
    alternativeRoutes = new ArrayList<>();
    boxesPerRoute = new ArrayList<>();
    boxes = new HashMap<>();
    // clear combo box
    startComboBox.getEditor().setText(null);
    destinationComboBox.getEditor().setText(null);
  }

  /**
   * adds all crashes from the given query to the map as markers
   *
   * @param query SQL Query object
   */
  private void setCrashesFromList(QueryBuilder<Crash> query) {
    javaScriptConnector.call("clearCrashes");

    new Thread(
            new Task<Void>() {
              @Override
              protected Void call() throws Exception {
                int page = 0;
                List<String> crashJSONList;
                do {
                  crashJSONList =
                      query.getPageAsJSON(
                          Arrays.asList("id", "lat", "lng", "crashSeverity"), page, 100000);
                  String execute = "addCrashMarkers([" + String.join(",", crashJSONList) + "])";
                  Platform.runLater(() -> webEngine.executeScript(execute));
                  // use this to pace the loop
                  // everything here runs pretty fast
                  // so it reaches the runLater quickly which causes the UI to freeze
                  // the delay lets the user move the map (ect.) when the data is being sent
                  TimeUnit.MILLISECONDS.sleep(300);
                  page++;
                } while (!crashJSONList.isEmpty());
                return null;
              }
            })
        .start();
  }

  /**
   * Handles the selection of a route from the routeComboBox.
   *
   * <p>When a route is selected from the ComboBox, this method retrieves the start and end
   * locations of the selected route and sets the corresponding text fields with these values.
   */
  @FXML
  private void selectRoute() {
    SavedRoute selectedRoute = routeComboBox.getSelectionModel().getSelectedItem();
    if (selectedRoute != null) {
      String start = selectedRoute.getStartLocation();
      String end = selectedRoute.getEndLocation();

      // Set the text fields with the retrieved values
      startComboBox.getEditor().setText(start);
      destinationComboBox.getEditor().setText(end);
    }
  }

  /**
   * Calculates and assigns ratings to alternative routes based on various factors and sorts them by
   * rating.
   */
  public void rateRoutes() {
    // Calculate ratings for all routes
    for (Route route : alternativeRoutes) {
      double rating = calculateRouteRating(route);
      route.setRating((int) rating);
    }
    alternativeRoutes.sort(Comparator.comparing(Route::getRating));
    int ratingCount = 0;
    for (int i = alternativeRoutes.size() - 1; i > -1; i--) {
      alternativeRoutes.get(i).setRating(alternativeRoutes.size() - (ratingCount++));
    }
  }

  /**
   * Calculates a rating for a route based on crash incidents, distance, and duration.
   *
   * @param route The route for which the rating is calculated.
   * @return The calculated route rating, ranging from 1 to 3.
   */
  private double calculateRouteRating(Route route) {
    double maxCrashes = maxCrashValue();
    double maxDistance = maxDistanceValue();
    double maxDuration = maxDurationValue();

    double normalizedCrashes = 0.1 * (1.0 - (route.getRouteCrashes() / maxCrashes));
    double normalizedDistance = 0.3 * (route.getRouteDistance() / maxDistance);
    double normalizedDuration = 0.3 * (1.0 - (route.getRouteDuration() / maxDuration));

    double rating = (normalizedCrashes + normalizedDistance + normalizedDuration) / 3.0 * 3.0;

    // Ensure the rating is between 0 and 3
    return Math.min(3, Math.max(1, rating));
  }
  /**
   * Finds the maximum number of crashes among the alternative routes.
   *
   * @return The maximum number of crashes.
   */
  private int maxCrashValue() {
    int crashes = 0;
    for (Route route : alternativeRoutes) {
      crashes = Math.max(crashes, route.getRouteCrashes());
    }
    return crashes;
  }

  /**
   * Finds the maximum route distance among the alternative routes.
   *
   * @return The maximum route distance in kilometers.
   */
  private double maxDistanceValue() {
    double distance = 0.0;
    for (Route route : alternativeRoutes) {
      distance = Math.max(distance, route.getRouteDistance());
    }
    return distance;
  }
  /**
   * Finds the maximum route duration among the alternative routes.
   *
   * @return The maximum route duration in minutes.
   */
  private double maxDurationValue() {
    double duration = 0.0;
    for (Route route : alternativeRoutes) {
      duration = Math.max(duration, route.getRouteDuration());
    }
    return duration;
  }

  /**
   * This method runs when the view button is clicked. It gets the text from the start and
   * destination fields. Then, it tries to find out if these locations are real. If both locations
   * are good, it builds routes between them. Otherwise, it shows warning messages.
   */
  @FXML
  private void onClickedView() {
    String startLocation = startComboBox.getEditor().getText();
    String destinationLocation = destinationComboBox.getEditor().getText();

    if (!verifyRoute(startLocation, destinationLocation)) {
      return; // Exit if the route is not valid
    }

    Position startingPosition = AddressPredictor.getPositionFromAddress(startLocation);
    Position destinationPosition = AddressPredictor.getPositionFromAddress(destinationLocation);
    if (boxes.size() != 0 || alternativeRoutes.size() != 0 || boxesPerRoute.size() != 0) {
      removeRouteFromMap();
    }
    generateAlternativeRoutes(startingPosition, destinationPosition);
    generateBoxes();
    addPointsOnRoute();
    rateRoutes();
    buildRoutes(startingPosition, destinationPosition);
  }

  /**
   * Generates boxes for each route segment and returns a map of start and end positions for each
   * segment.
   *
   * @return A HashMap with start and end positions for each route segment.
   */
  public HashMap<List<Position>, List<Position>> generateBoxes() {
    HashMap<List<Position>, List<Position>> totalBoxesPerRoute = new HashMap<>();
    boxesPerRoute = new ArrayList<>();
    boxes = new HashMap<>();
    for (Route route : alternativeRoutes) {
      for (int i = 0; i < route.length() - 1; i++) {
        Position start = route.getPoints().get(i);
        Position end = route.getPoints().get(i + 1);
        RouteSegementBox box = new RouteSegementBox(start, end);
        HashMap<ArrayList<Position>, ArrayList<Position>> result = box.boxCoordinateList();
        boxes.put(result.keySet().iterator().next(), result.values().iterator().next());
        totalBoxesPerRoute.put(
            result.keySet().iterator().next(), result.values().iterator().next());
      }
      boxesPerRoute.add(totalBoxesPerRoute);
      totalBoxesPerRoute = new HashMap<>();
    }
    return boxes;
  }

  /**
   * Creates and displays different routes from the starting position to the destination. For each
   * route, there's a delay before it's shown. After showing all routes, it does some additional
   * modifications.
   *
   * @param startingPosition The starting point for the route.
   * @param destinationPosition The endpoint of the route.
   */
  private void buildRoutes(Position startingPosition, Position destinationPosition) {

    for (Route route : alternativeRoutes) {
      displayRouteOnMap(route, route.getId(), route.getRating());
    }
  }

  /**
   * Displays route information for a specific route on the crash card. makes use of the
   * javascriptConnector to call the displayRouteInfo method in the javacript file.
   *
   * @param id The ID of the route to be displayed on the crash card.
   */
  public void addCrashCard(int id) {
    for (Route route : alternativeRoutes) {
      if (route.getId() == id) {
        javaScriptConnector.call(
            "displayRouteInf",
            alternativeRoutes.size(),
            route.getRating(),
            route.getRouteDistance(),
            route.getRouteDuration(),
            route.getRouteCrashes(),
            route.getElivation(),
            route.getAverageCrashPerKmOnRoute(),
            route.getInstruction().toString());
        break;
      }
    }
  }

  /**
   * Adds markers for crash positions along the routes, filters the bounding box, and sets all route
   * crashes via JavaScript.
   *
   * <p>This method iterates through the generated route segment boxes and retrieves positions
   * within each box using a savedRouteDAO. It then adds route markers for each crash position and
   * sets all route crashes using a JavaScript connector.
   */
  public void addPointsOnRoute() {
    List<Position> crashes = new ArrayList<>();
    int numOfCrashes = 0;
    int counter = 0;
    for (HashMap<List<Position>, List<Position>> boxes : boxesPerRoute) {
      for (Map.Entry<List<Position>, List<Position>> entry : boxes.entrySet()) {
        List<Position> positions = entry.getKey();
        List<Position> position =
            savedRouteDAO.filterBoundingBox(
                positions.get(0), positions.get(1), positions.get(2), positions.get(3));
        for (Position crashPos : position) {
          numOfCrashes++;
          if (!crashes.contains(crashPos)) {
            javaScriptConnector.call(
                "addRouteMarkerToMarkers", crashPos.getLat(), crashPos.getLng());
            crashes.add(crashPos);
          }
        }
      }
      alternativeRoutes.get(counter++).setRouteCrashes(numOfCrashes);
      Double routeDistance = alternativeRoutes.get(counter - 1).getRouteDistance();
      alternativeRoutes.get(counter - 1).setAverageCrashPerKmOnRoute(numOfCrashes / routeDistance);
      numOfCrashes = 0;
    }
    javaScriptConnector.call("setAllRouteCrash");
  }
  /**
   * Fetches alternative bike routes between a starting and ending position from an external API.
   * The method constructs a URL based on the start and end positions and sends a GET request to the
   * API. Once the response is received, it is parsed to extract route details in JSON format. The
   * parsed routes are then converted into Route objects and added to a list. If any issues arise,
   * like an invalid response from the API or a parsing error, the method returns an empty list.
   *
   * @param start The starting location specified as a Position object with latitude and longitude.
   * @param end The ending location specified as a Position object with latitude and longitude.
   * @return A list of alternative routes (as Route objects) between the start and end positions, or
   *     an empty list if there's an error.
   */
  private List<Route> generateAlternativeRoutes(Position start, Position end) {
    alternativeRoutes = new ArrayList<>();
    ArrayList<String> routeInstructions = new ArrayList<>();
    int routeId = 0;
    String apiUrlFormat =
        "https://graphhopper.com/api/1/route?"
            + "point="
            + start.getLat()
            + ","
            + start.getLng()
            + "&point="
            + end.getLat()
            + ","
            + end.getLng()
            + "&vehicle=bike&algorithm=alternative_route&points_encoded=false&elevation=true&alternative_route.max_paths=3&key=8643ea38-0e88-4384-b457-3bcc05bf854a";
    try {
      URL url = new URL(apiUrlFormat);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder responseContent = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
          responseContent.append(inputLine);
        }
        in.close();
        String jsonResponse = responseContent.toString();
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(jsonResponse);

        if (obj instanceof JSONObject jsonObject) {
          JSONArray routesArray = (JSONArray) jsonObject.get("paths");
          for (Object routeObj : routesArray) {
            JSONObject route = (JSONObject) routeObj;
            JSONArray instructionArray = (JSONArray) route.get("instructions");
            for (Object instructionObj : instructionArray) {
              JSONObject instruction = (JSONObject) instructionObj;
              String text = (String) instruction.get("text");
              routeInstructions.add(text.toString());
            }
            JSONObject geometry = (JSONObject) route.get("points");
            JSONArray coordinates = (JSONArray) geometry.get("coordinates");
            double routeDistance = (double) route.get("distance") / 1000;
            long routeDuration = (long) route.get("time") / 1000 / 60;
            List<Position> decodedCoordinates = new ArrayList<>();
            List<Double> routeElivation = new ArrayList<>();
            for (Object coordObj : coordinates) {
              JSONArray coordArray = (JSONArray) coordObj;
              double lng = (double) coordArray.get(0);
              double lat = (double) coordArray.get(1);
              double elivationOfCoordinate = (double) coordArray.get(2);
              Position position = new Position(lat, lng);
              decodedCoordinates.add(position);
              routeElivation.add(elivationOfCoordinate);
            }
            Double AverageElevation = calculateAverageElevation(routeElivation);
            Route routeObject =
                new Route(
                    routeId++,
                    0,
                    routeDistance,
                    (int) routeDuration,
                    0,
                    routeInstructions,
                    AverageElevation,
                    0.0,
                    decodedCoordinates.toArray(new Position[0]));
            alternativeRoutes.add(routeObject);
            routeInstructions = new ArrayList<>();
          }
        } else {
          log.info("Invalid JSON response");
        }
      } else {
        log.info("GET request failed. Response Code: " + responseCode);
      }
    } catch (IOException | ParseException e) {
      e.printStackTrace();
    }

    return alternativeRoutes;
  }
  /**
   * Calculates the average elevation from a list of elevation coordinates.
   *
   * @param elevationCoordinates A list of elevation coordinates.
   * @return The average elevation, or 0.0 if the list is empty.
   */
  public double calculateAverageElevation(List<Double> elevationCoordinates) {
    double sum = 0.0;
    int count = elevationCoordinates.size();
    for (double elevation : elevationCoordinates) {
      sum += elevation;
    }
    if (count > 0) {
      return sum / count;
    } else {
      return 0.0;
    }
  }

  /** call javascript for toggling points on (and turning points off if they are on) */
  @FXML
  private void togglePoints() {
    log.info("toggled points");

    if (getPointsOn()) {
      setPointsOn(false);
    } else {
      setPointsOn(true);
      setHeatMapOn(false);
    }


    javaScriptConnector.call("togglePoints", getPointsOn());
  }

  /** call javascript for toggling heatmap on (and turning points off if they are on) */
  @FXML
  private void toggleHeatmap() {
    log.info("toggled heatmap");

    if (getHeatMapOn()) {
      setHeatMapOn(false);
    } else {
      setHeatMapOn(true);
      setPointsOn(false);
    }

    javaScriptConnector.call("toggleHeatMap", getHeatMapOn());
  }

  /**
   * call js to set the initial map placement to the given lat and lng values
   *
   * @param lat latitude of point
   * @param lng longitude of point
   */
  public void initialMapConfiguration(float lat, float lng) {
    javaScriptConnector.call("initialMapConfiguration", lat, lng);
  }

  /**
   * @return whether the map has been initalised or not
   */
  public boolean getMapExists() {
    return mapExists;
  }

  /**
   * goes to the specified address when the go button is pressed
   */
  @FXML
  public void goToAddress(){
    javaScriptConnector.call("initialMapConfiguration", AddressPredictor.getPositionFromAddress(goToComboBox.getEditor().getText()).getLat(), AddressPredictor.getPositionFromAddress(goToComboBox.getEditor().getText()).getLng());

  }
}
