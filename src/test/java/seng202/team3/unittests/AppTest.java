package seng202.team3.unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seng202.team3.App;
import seng202.team3.Database;
import seng202.team3.dao.CrashDAO;
import seng202.team3.exceptions.InstanceAlreadyExistsException;

public class AppTest {
  @TempDir static Path tempDir;

  @BeforeAll
  static void resetAndUseInMemoryDB() throws InstanceAlreadyExistsException {
    Database.RESET();
    Database.getDatabaseWithPath(tempDir.resolve("database.db").toString());
  }

  @Test
  public void testAppLoadCSV() {
    App.main(new String[] {"load", "./crash_data_10k.csv"});

    CrashDAO crashDAO = new CrashDAO();

    assertEquals(10255, crashDAO.getAll().size());
  }
}
