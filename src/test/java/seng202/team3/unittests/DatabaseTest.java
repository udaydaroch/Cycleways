package seng202.team3.unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.Database;
import seng202.team3.exceptions.InstanceAlreadyExistsException;

public class DatabaseTest {
  @BeforeEach
  void clearDatabaseInstance() {
    Database.RESET();
  }

  @Test
  void GetDatabase__AlwaysReturnsSameInstance() {
    Database db1 = Database.getDatabase();
    Database db2 = Database.getDatabase();

    assertEquals(db1.hashCode(), db2.hashCode());
  }

  @Test
  void GetDatabaseWithPage__CanNotBeCalledTwice() throws InstanceAlreadyExistsException {
    Database.getDatabaseWithPath(":memory:");

    Exception exception =
        assertThrows(
            InstanceAlreadyExistsException.class,
            () -> {
              Database.getDatabaseWithPath(":memory:");
            });

    assertTrue(exception.getMessage().startsWith("Database instance already set"));
  }

  @Test
  void GetDatabaseWithPage__CanBeCalledTwiceIfRESETUsedAndTheInstancesAreDiffrent()
      throws InstanceAlreadyExistsException {
    Database db1 = Database.getDatabaseWithPath(":memory:");
    Database.RESET();
    Database db2 = Database.getDatabaseWithPath(":memory:");

    assertNotEquals(db1.hashCode(), db2.hashCode());
  }
}
