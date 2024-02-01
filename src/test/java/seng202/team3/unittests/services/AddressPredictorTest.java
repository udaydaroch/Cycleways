package seng202.team3.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import seng202.team3.models.Position;
import seng202.team3.services.AddressPredictor;
import seng202.team3.services.RouteSegementBox;


public class AddressPredictorTest {
  private static final Logger log = LogManager.getLogger(RouteSegementBox.class);

  @Test
  public void predictAddresses20IlamRoad__Gets2DistinctAddresses() {
    // Test a distinct valid address in New Zealand
    String inputAddress = "20 Ilam Road";
    List<String> predictedAddresses = AddressPredictor.predictAddresses(inputAddress, 100);

    assertEquals(2, predictedAddresses.size());
    assertEquals(
        "20, Ilam Road, Upper Riccarton, Christchurch, Christchurch City, Canterbury, 8041",
        predictedAddresses.get(0));
    assertEquals(
        "20, Maidstone Road, Ilam, Fendalton-Waimari-Harewood Community, Christchurch, Christchurch"
            + " City, Canterbury, 8041",
        predictedAddresses.get(1));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 15})
  public void predictAddressesNResultsHighStreet__OnlyGetsNResults(Integer n) {
    // main street has lots of results
    List<String> predictedAddresses = AddressPredictor.predictAddresses("high street", n);
    // we should only get n
    assertEquals(n, predictedAddresses.size());
  }

  @Test
  public void
      predictAddresses15ResultsMainStreet__OnlyGets11ResultsBecauseThereAreSomeDuplicates() {
    // main street has lots of results
    List<String> predictedAddresses = AddressPredictor.predictAddresses("main street", 15);
    // we should only get 11 because there are duplicate main streets in result that get removed
    assertEquals(11, predictedAddresses.size());
  }

  @Test
  public void predictAddressedCanterbury__OnlyReturnsResultsFromNewZealand() {
    // Canterbury is in New Zealand but also the UK
    List<String> predictedAddresses = AddressPredictor.predictAddresses("Canterbury", 100);
    assertEquals(predictedAddresses.size(), 1);
    assertEquals("Canterbury", predictedAddresses.get(0));
  }

  @Test
  public void predictAddressedEmptyString__ReturnsEmptyList() {
    // Canterbury is in New Zealand but also the UK
    List<String> predictedAddresses = AddressPredictor.predictAddresses("", 100);
    assertTrue(predictedAddresses.isEmpty());
  }

  @Test
  public void predictAddressedWhitespace__ReturnsEmptyList() {
    // Canterbury is in New Zealand but also the UK
    List<String> predictedAddresses = AddressPredictor.predictAddresses("    ", 100);
    assertTrue(predictedAddresses.isEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = {"Te Pa", "Te Pā"})
  public void predictAddressesTePa__ReturnsResultsWithAccentRemoved() {
    List<String> predictedAddresses = AddressPredictor.predictAddresses("Te Pa", 100);
    log.info(predictedAddresses);
    assertTrue(
        predictedAddresses.contains(
            "Te Pa o Rakaihautu, 7, McLean Street, North Linwood, Christchurch, Christchurch City,"
                + " Canterbury, 8062"));
  }

  @Test
  public void getPositionFromAddressIlamRoad__HasCorrectPosition() {
    Position position = AddressPredictor.getPositionFromAddress("Ilam road");
    assertEquals(-43.5311922, position.getLat());
    assertEquals(172.5801849, position.getLng());
  }

  @Test
  public void getPositionFromAddressNonExistentAddress__ReturnsNull() {
    Position position =
        AddressPredictor.getPositionFromAddress("Non existent road that cannot be found");
    assertNull(position);
  }

  @Test
  public void getPositionFromAddressEmptyString__ReturnsNull() {
    Position position = AddressPredictor.getPositionFromAddress("");
    assertNull(position);
  }

  @Test
  public void getPositionFromAddressWhitespaceString__ReturnsNull() {
    Position position = AddressPredictor.getPositionFromAddress("    ");
    assertNull(position);
  }
}
