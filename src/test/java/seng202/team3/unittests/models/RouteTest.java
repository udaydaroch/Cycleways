package seng202.team3.unittests.models;

import org.junit.jupiter.api.Test;
import seng202.team3.models.Position;
import seng202.team3.models.Route;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RouteTest {
  Position first = new Position(1.1, 1.2);
  Position last = new Position(3.1, 3.2);
  Position middle = new Position(2.1, 2.1);
  ArrayList<String> instruction =  new ArrayList<>();
  Route route = new Route(0,0, 0.1, 0, 0,instruction,0.0,0.0 ,first, middle, last);

  @Test
  void allPositionsShouldBeIncludedInJSON() {
    assertEquals(
        "[{\"lat\": 1.100000, \"lng\": 1.200000}, {\"lat\": 2.100000, \"lng\": 2.100000}, {\"lat\":"
            + " 3.100000, \"lng\": 3.200000}]",
        route.toJSONArray());
  }

  @Test
  void getEnd__GetsLastPosition() {
    assertEquals(last, route.getEnd());
  }

  @Test
  void getStart__GetsFirstPosition() {
    assertEquals(first, route.getStart());
  }

  @Test
  void getPoints__GetsAllPositionsAsList() {
    assertEquals(route.getPoints(), Arrays.asList(first, middle, last));
  }
}
