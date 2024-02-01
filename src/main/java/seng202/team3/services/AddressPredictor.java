package seng202.team3.services;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team3.models.Position;

/**
 * class to deal with predicting incomplete and incorrect addresses in address search bar.
 *
 * @author Ryan Scofield
 * @author Hanan Fokkens
 * @author Morgan English
 */
public class AddressPredictor {

  private static final Logger log = LogManager.getLogger(AddressPredictor.class);

  /**
   * Gets a list of possible addresses given a string that may or may not be a valid address.
   *
   * @param address The address used to get the prediction, this may not be a valid address.
   * @param limit Limit the number of returned addresses.
   * @return List of addresses that matched the initial address.
   */
  public static List<String> predictAddresses(String address, Integer limit) {
    List<String> predictedAddresses = new ArrayList<>();

    String logMessage =
        String.format("Requesting geolocation from Nominatim for address: %s", address);
    log.info(logMessage);

    try {
      JSONArray jsonArray = getNearAddresses(address, limit);

      for (Object obj : jsonArray) {
        JSONObject jsonObject = (JSONObject) obj;
        Object addressDisplayName = jsonObject.get("display_name");
        String addressAsString = addressDisplayName.toString();
        addressAsString = normalizeAddress(addressAsString);
        addressAsString = addressAsString.replaceAll(", New Zealand / Aotearoa", "");
        predictedAddresses.add(addressAsString);
      }
    } catch (IOException | InterruptedException | ParseException e) {
      log.error(e);
    }

    return predictedAddresses;
  }

  /**
   * Gets the position of an address. If the address cannot be found then null is returned.
   *
   * @param address The address to search for
   * @return The position of the address or null when the address is not found.
   */
  public static Position getPositionFromAddress(String address) {
    try {
      JSONArray jsonArray = getNearAddresses(address, 1);

      if (jsonArray.isEmpty()) {
        return null;
      }

      JSONObject bestResult = (JSONObject) jsonArray.get(0);
      Double lat = Double.parseDouble((String) bestResult.get("lat"));
      Double lon = Double.parseDouble((String) bestResult.get("lon"));

      return new Position(lat, lon);
    } catch (IOException | InterruptedException | ParseException e) {
      log.error(e);
      return null;
    }
  }

  /***
   * request near matching addresses from openstreetmap
   *
   * @param address Base address to search from
   * @param limit   Maximum number of addresses returned
   * @return A JSONArray containing near-matching addresses
   * @throws IOException          When an I/O error occurs during the HTTP request
   * @throws InterruptedException When the thread is interrupted while waiting for
   *                              the request to complete
   * @throws ParseException       When there is an error parsing the JSON response
   */
  private static JSONArray getNearAddresses(String address, Integer limit)
      throws IOException, InterruptedException, ParseException {
    // Creating the http request
    HttpClient client = HttpClient.newHttpClient();
    String urlAddress = URLEncoder.encode(address.trim(), StandardCharsets.UTF_8);
    URI url =
        URI.create(
            "https://nominatim.openstreetmap.org/search?q="
                + urlAddress
                + "&countrycodes=nz&format=json&limit="
                + limit);
    HttpRequest request = HttpRequest.newBuilder(url).build();

    // Getting the response
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    log.info("response -" + response);

    // Parsing the json response to get the latitude and longitude co-ordinates
    JSONParser parser = new JSONParser();
    return (JSONArray) parser.parse(response.body());
  }

  /***
   * Takes an address and removes all unicode character and replaces them with
   * ascii characters
   *
   * @param address The address to be normalized
   * @return The address normalized, removing unicode and replacing accented
   *         characters their non-accented versions
   */
  private static String normalizeAddress(String address) {
    // See: https://stackoverflow.com/a/3322174/22476307
    return Normalizer.normalize(address, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
  }

    /**
     * Sets up an address predictor based on user input.
     *
     * @param comboBox The ComboBox to set up as an address predictor.
     */
  public static void setupAddressPredictorComboBox(ComboBox<String> comboBox) {
    ObservableList<String> predictionData = FXCollections.observableArrayList();
    Timeline debounceTimer =
        new Timeline(
            new KeyFrame(
                Duration.millis(800),
                e -> {
                  String search = comboBox.getEditor().getText().trim();

                  if (search.isBlank()
                      || (comboBox.getProperties().containsKey("last")
                          && comboBox.getProperties().get("last").equals(search))) {
                    return;
                  } else {
                    comboBox.getProperties().put("last", search);
                  }

                  predictionData.setAll(predictAddresses(search, 5));
                  comboBox.getEditor().setText(search);
                  comboBox.getEditor().selectEnd();
                  comboBox.getEditor().selectEnd();
                  comboBox.getEditor().deselect();
                }));

    comboBox.setEditable(true);
    comboBox.setItems(predictionData);

    comboBox
        .getEditor()
        .addEventFilter(
            KeyEvent.KEY_RELEASED,
            (e1) -> {
              KeyCode code = e1.getCode();

              if (code.isLetterKey() || code.isDigitKey() || code.isWhitespaceKey()) {
                comboBox.hide();
                debounceTimer.stop();
                debounceTimer.play();
                comboBox.show();
              }

              e1.consume();
            });

    comboBox
        .focusedProperty()
        .addListener(
            (ov, newValue, oldValue) -> {
              if (newValue) {
                comboBox.show();
              }
            });
  }
}
