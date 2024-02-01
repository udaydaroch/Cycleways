package seng202.team3;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.dao.CrashDAO;
import seng202.team3.gui.MainWindow;
import seng202.team3.models.Crash;

/**
 * Default entry point class
 *
 * @author seng202 teaching team
 */
public class App {
  private static final Logger log = LogManager.getLogger(App.class);

  /**
   * Load in some CSV data in to the applications database.
   *
   * @param path
   */
  private static void load(String path) {
    ImportCSV importer = new ImportCSV();
    CrashDAO crashDAO = new CrashDAO();

    List<Crash> dataFromCSV = importer.readAllDataFromFile(path);

    crashDAO.addBatch(dataFromCSV);

    List<Crash> dataFromDB = crashDAO.getAll();

    if (dataFromCSV.size() != dataFromDB.size()) {
      log.error("Faild load all crashes.");
    }

    log.info("Loaded %,d out of %,d crashes".formatted(dataFromDB.size(), dataFromCSV.size()));
  }

  /**
   * Entry point which runs the javaFX application Also shows off some different logging levels
   *
   * @param args program arguments from command line
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      log.info("Started Main");
      MainWindow.main(args);
    } else if (args[0].equals("load")) {
      load(args[1]);
    }
  }
}
