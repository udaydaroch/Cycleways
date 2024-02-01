package seng202.team3.unittests.models;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import seng202.team3.models.Position;

public class PositionTest {
  @Test
  void checkToJSON__IncludesLngAndLat() {
    Position pos = new Position(1.1, 1.2);
    assertEquals("{\"lat\": 1.100000, \"lng\": 1.200000}", pos.toJSON());
  }
}
