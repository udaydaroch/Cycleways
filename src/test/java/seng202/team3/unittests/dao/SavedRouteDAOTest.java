package seng202.team3.unittests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seng202.team3.Database;
import seng202.team3.dao.SavedRouteDAO;
import seng202.team3.exceptions.InstanceAlreadyExistsException;
import seng202.team3.models.Position;
import seng202.team3.models.SavedRoute;

public class SavedRouteDAOTest {
  @TempDir static Path tempDir;
  SavedRouteDAO savedRouteDAO;

  @BeforeAll
  static void beforeAll() throws InstanceAlreadyExistsException {
    Database.RESET();
    Database.getDatabaseWithPath(tempDir.resolve("database.db").toString());
  }

  @BeforeEach
  void beforeEach() {
    savedRouteDAO = new SavedRouteDAO();
  }

  @Test
  public void testAddSavedRouteFromDatabase() {
    int newId = savedRouteDAO.add(new SavedRoute("Ilam road", "Riccarton road"));

    assertEquals(1, newId);
    assertEquals(
        savedRouteDAO.getAll(), Arrays.asList(new SavedRoute(1, "Ilam road", "Riccarton road")));
  }

  @Test
  public void testDeleteSavedRouteFromDatabase() {
    // insert a direction
    int newId = savedRouteDAO.add(new SavedRoute("Hamilton Ave", "Maidstone road"));

    SavedRoute result = savedRouteDAO.query().filterEqual("id", newId).getFirst();
    assertNotNull(result);

    savedRouteDAO.delete(result);

    SavedRoute resultAfterDelete = savedRouteDAO.query().filterEqual("id", newId).getFirst();
    assertNull(resultAfterDelete);
  }

  @Test
  public void testPointInsidePolygonReturnsTrue() {
    Position A = new Position(1, 1);
    Position B = new Position(1, 4);
    Position C = new Position(4, 4);
    Position D = new Position(4, 1);

    Position pointInside = new Position(2, 2);

    assertTrue(SavedRouteDAO.isInsidePolygon(A, B, C, D, pointInside));
  }

  @Test
  public void testPointOutsidePolygonReturnsFalse() {
    Position A = new Position(1, 1);
    Position B = new Position(1, 4);
    Position C = new Position(4, 4);
    Position D = new Position(4, 1);

    Position pointOutside = new Position(5, 5);
    assertFalse(SavedRouteDAO.isInsidePolygon(A, B, C, D, pointOutside));
  }

  @Test
  public void SavedRouteIsNotUpdatable() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> savedRouteDAO.update(new SavedRoute(0, "Ilam", "Belfast")));
  }
}
