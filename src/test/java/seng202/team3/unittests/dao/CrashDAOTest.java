package seng202.team3.unittests.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seng202.team3.Database;
import seng202.team3.dao.CrashDAO;
import seng202.team3.exceptions.InstanceAlreadyExistsException;
import seng202.team3.models.Crash;
import seng202.team3.services.AddressPredictor;

public class CrashDAOTest {
  @TempDir static Path tempDir;
  private static final Logger log = LogManager.getLogger(AddressPredictor.class);
  CrashDAO crashDAO;

  @BeforeAll
  static void beforeAll() throws InstanceAlreadyExistsException {
    Database.RESET();
    Database.getDatabaseWithPath(tempDir.resolve("database.db").toString());
    new CrashDAO()
        .addBatch(
            Arrays.asList(
                Crash.createTestCrash(1, 0),
                Crash.createTestCrash(2, 1),
                Crash.createTestCrash(3, 2),
                Crash.createTestCrash(4, 3),
                Crash.createTestCrash(5, 1),
                Crash.createTestCrash(6, 2),
                Crash.createTestCrash(7, 0),
                Crash.createTestCrash(8, 1),
                Crash.createTestCrash(9, 100)));
      log.info(tempDir.resolve("database.db").toString());
  }

  @BeforeEach
  void beforeEach() {
    crashDAO = new CrashDAO();
  }

  @Test
  void checkRightCrashesAvaliable() {
    List<Crash> results = crashDAO.getAll();

    Assertions.assertEquals(9, results.size());
  }

  @Test
  void getAttributeSeverity__ShouldReturnEachValueInSeverityColumn() {
    List<String> results = crashDAO.getAttributes("crashSeverity");
    Assertions.assertEquals(Arrays.asList("-1", "0", "1", "2", "3"), results);
  }

  @Test
  void checkCanNotDeleteCrash() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> crashDAO.delete(Crash.createTestCrash(90, 4)));
  }

  @Test
  void checkCanNotUpdateCrash() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> crashDAO.update(Crash.createTestCrash(90, 4)));
  }

  @Test
  void checkAddActuallyAddsACrashToDatabase() {
    Integer id = crashDAO.add(Crash.createTestCrash(20, 4));

    Assertions.assertEquals((Integer) 20, id);
    Assertions.assertEquals((Integer) 10, crashDAO.query().getAllCount());
  }
}
