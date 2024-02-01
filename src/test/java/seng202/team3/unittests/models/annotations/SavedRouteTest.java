package seng202.team3.unittests.models.annotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.models.SavedRoute;

public class SavedRouteTest {

  private SavedRoute savedRoute;

  @BeforeEach
  void init() {
    savedRoute = new SavedRoute("Start", "End");
  }

  @Test
  void testConstructor() {
    assertEquals("Start", savedRoute.getStartLocation());
  }

  @Test
  void testGetStartLocation() {
    assertEquals("Start", savedRoute.getStartLocation());
  }

  @Test
  void testToString() {
    String expectedString = "From Start" + System.lineSeparator() + "To End";
    assertEquals(expectedString, savedRoute.toString());
  }
}
