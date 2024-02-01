package seng202.team3.services;

import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.dao.CrashDAO;

/**
 * Simple example class showing the ability to 'bridge' from javascript to java The functions within
 * can be called from our javascript in the map view when we set an object of this class as a member
 * of the javascript Note: This is a very basic example you can use any java code, though you may
 * need to be careful when working with objects
 *
 * @author Morgan English
 */
public class JavaScriptBridge {
  private static final Logger log = LogManager.getLogger(JavaScriptBridge.class);
  private CrashDAO crashDAO = new CrashDAO();
  private JavaScriptBridgePollylineClickCallback polyLineInfoCallback;

  public JavaScriptBridge(JavaScriptBridgePollylineClickCallback polyLineInfoCallback) {
    this.polyLineInfoCallback = polyLineInfoCallback;
  }

  /**
   * Used to pass logs from javascript to java.
   *
   * @param message The javascript log message
   */
  public void consoleLog(String message) {
    log.info("[console] " + message);
  }

  public void polyLineInfoCall(int polyLineID) {
    polyLineInfoCallback.run(polyLineID);
  }

  /**
   * Called to get the JSON string for a crash
   *
   * @param crashId ID of given crash
   * @return JSON string of given crash
   */
  public String getCrashJSONString(Integer crashId) {
    log.info("crash " + crashId);
    List<String> results =
        crashDAO
            .query()
            .filterEqual("id", crashId)
            .getPageAsJSON(
                Arrays.asList(
                    "id",
                    "lat",
                    "lng",
                    "crashSeverity",
                    "weather",
                    "crashLocation1",
                    "crashLocation2",
                    "bicycle",
                    "crashYear"),
                0,
                1);

    if (results.isEmpty()) {
      return "{'id': " + crashId + "}";
    } else {
      return results.get(0);
    }
  }
}
