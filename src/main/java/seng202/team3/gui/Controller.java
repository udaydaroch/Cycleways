package seng202.team3.gui;

import java.util.List;
import javafx.scene.control.Alert;
import seng202.team3.dao.CrashDAO;

/**
 * Base controller class
 *
 * @author Hanan Fokkens
 * @author Charlie Porter
 * @author Tobias Paull
 */
public abstract class Controller {
  /** Shared access to crash dao */
  public CrashDAO crashDAO = new CrashDAO();

  protected LayoutController layoutController;

  /**
   * Initializes the current controller with the provided LayoutController instance.
   *
   * @param incomingLayoutController The LayoutController to be set as the layoutController.
   */protected void initialize(LayoutController incomingLayoutController) {
    layoutController = incomingLayoutController;
  }

  /**
   * Returns list of all region names stored in database
   *
   * @return List of all regions.
   */
  public List<String> getRegions() {
    return crashDAO.getAttributes("region");
  }

  /**
   * Displays a warning dialog with the provided message. If the message is null or empty, a default
   * warning message "You can't do that." will be shown.
   *
   * @param message The warning message to be displayed in the dialog.
   */
  public void showWarning(String message) {
    Alert alert = new Alert(Alert.AlertType.WARNING);

    alert.setTitle("CycleWays");
    alert.setHeaderText("Warning!");

    // Check if a message is provided; if not, use the default message
    if (message == null || message.trim().isEmpty()) {
      message = "You can't do that.";
    }
    alert.setContentText(message);
    alert
        .getDialogPane()
        .getStylesheets()
        .add(getClass().getResource("/css/alert.css").toExternalForm());
    alert.showAndWait();
  }

  /** Called by the layout when the user changes to this controller. */
  public void pageChanged() {}
}
