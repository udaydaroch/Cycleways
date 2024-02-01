package seng202.team3.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.util.Pair;
import org.junit.jupiter.api.io.TempDir;
import seng202.team3.App;
import seng202.team3.Database;
import seng202.team3.exceptions.InstanceAlreadyExistsException;
import seng202.team3.models.Crash;
import seng202.team3.services.CrashFilterManager;
import seng202.team3.services.CrashTableManager;

public class CrashTableManagerSorting {
  @TempDir static Path tempDir;
  private CrashFilterManager crashFilterManager;
  private CrashTableManager crashTableManager;

  String lastSorted = "";
  Boolean lastSortedReverse = false;

  @BeforeAll
  static void setupDatabase() throws InstanceAlreadyExistsException, IOException {
    File tempFile = File.createTempFile("database-", ".db");
    System.out.println(tempFile);
    Database.RESET();
    Database.getDatabaseWithPath(tempFile.getAbsolutePath());
    App.main(new String[] {"load", "./crash_data_10k.csv"});
  }

  @Given("the table is loaded with all data and page size {int}")
  public void loadTableDataAndSetPageSize(int pageSize) throws IOException {
    crashFilterManager = new CrashFilterManager();
    crashTableManager = new CrashTableManager(crashFilterManager);
    crashTableManager.setPageSize(pageSize);
  }

  @Given("the table is already sorted by a column {string}")
  @When("the {string} column is clicked")
  public void setTableSort(String column) {
    boolean reverse = (lastSorted != null) && (lastSorted == column);
    if (lastSortedReverse == true && lastSorted == column) {
      lastSorted = "";
      lastSortedReverse = false;
    } else {
      crashTableManager.setSorts(Arrays.asList(new Pair<>(column, reverse)));
      lastSorted = column;
      lastSortedReverse = reverse;
    }
  }

  private List<Crash> sortTableDataByColumn(String column, Boolean reverse) {
    List<Crash> tableDataSorted = new ArrayList<>(crashTableManager.getTableData());
    Collections.sort(
        tableDataSorted,
        (a, b) -> {
          switch (column) {
            case "crashYear":
              return a.getCrashYear().compareTo(b.getCrashYear());
            case "location":
              return a.getLocation().compareTo(b.getLocation());
            default:
              return 0;
          }
        });
    if (reverse) Collections.reverse(tableDataSorted);
    return tableDataSorted;
  }

  @Then("the table data is sorted by {string}")
  public void checkTableDataSortedByColumn(String column) {
    assertEquals(sortTableDataByColumn(column, false), crashTableManager.getTableData());
  }

  @Then("the table data is sorted by {string} in reverse")
  public void checkTableDataSortedByColumnInReverse(String column) {
    assertEquals(sortTableDataByColumn(column, true), crashTableManager.getTableData());
  }

  @Then("the table data is not sorted by {string}")
  public void crashTableDataNotSortedByColumn(String column) {
    assertNotSame(sortTableDataByColumn(column, true), crashTableManager.getTableData());
  }
}
