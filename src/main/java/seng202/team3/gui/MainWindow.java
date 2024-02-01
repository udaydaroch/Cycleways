package seng202.team3.gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class starts the javaFX application window
 *
 * @author seng202 teaching team
 */
public class MainWindow extends Application {

  /**
   * Opens the gui with the fxml content specified in resources/fxml/main.fxml
   *
   * @param primaryStage The current fxml stage, handled by javaFX Application class
   * @throws IOException if there is an issue loading fxml file
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
    FXMLLoader layoutLoader = new FXMLLoader(getClass().getResource("/fxml/layout.fxml"));
    Parent root = layoutLoader.load();

    LayoutController layoutController = layoutLoader.getController();

    // lanky but pre-load breaks - javafx.fxml.LoadException: No controller"
    layoutController.changeToTable();
    // make sure the map is available since the table depends on it
    layoutController.preLoad("map");

    primaryStage.setTitle("CycleWays");
    primaryStage.setMinWidth(800);
    primaryStage.setMinHeight(600);
    Scene scene = new Scene(root, 1500, 900);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Launches the FXML application, this must be called from another class (in this cass App.java)
   * otherwise JavaFX errors out and does not run
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}
