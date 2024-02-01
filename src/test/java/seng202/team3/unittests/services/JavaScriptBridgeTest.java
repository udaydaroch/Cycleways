package seng202.team3.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seng202.team3.Database;
import seng202.team3.dao.CrashDAO;
import seng202.team3.exceptions.InstanceAlreadyExistsException;
import seng202.team3.models.Crash;
import seng202.team3.services.JavaScriptBridge;

public class JavaScriptBridgeTest {
  @TempDir static Path tempDir;

  @BeforeAll
  static void beforeAll() throws InstanceAlreadyExistsException {
    Database.RESET();
    Database.getDatabaseWithPath(tempDir.resolve("database.db").toString());
    new CrashDAO().addBatch(List.of(Crash.createTestCrash(1, 1)));
  }

  @Test
  void getCrashJSONStringWithNonExistentId__ReturnOnlyIdInJSON() {
    JavaScriptBridge jsBridge = new JavaScriptBridge((id) -> {});

    assertEquals("{'id': 10}", jsBridge.getCrashJSONString(10));
  }

  @Test
  void getCrashJSONString__ReturnsCorrectJSONForCrash() {
    JavaScriptBridge jsBridge = new JavaScriptBridge((id) -> {});

    assertEquals(
        "{\"id\":1,\"lat\":172.579071044922,\"lng\":-43.5197257995605,\"crashSeverity\":1,"
            + "\"weather\":\"\",\"crashLocation1\":\"Ilam"
            + " Road\",\"crashLocation2\":\"Maidstone Road\",\"bicycle\":0,\"crashYear\":2023}",
        jsBridge.getCrashJSONString(1));
  }
}
