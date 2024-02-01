package seng202.team3.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import seng202.team3.App;
import seng202.team3.Database;
import seng202.team3.exceptions.InstanceAlreadyExistsException;
import seng202.team3.models.Crash;
import seng202.team3.services.CrashFilterManager;
import seng202.team3.services.CrashTableManager;

public class CrashTableManagerTest {
  @TempDir static Path tempDir;
  CrashTableManager crashTableManager;
  CrashFilterManager crashFilterManager = new CrashFilterManager();

  @BeforeAll
  static void BeforeAll() throws InstanceAlreadyExistsException {
    Database.RESET();
    Database.getDatabaseWithPath(tempDir.resolve("database.db").toString());

    // load in the 10k
    App.main(new String[] {"load", "./crash_data_10k.csv"});
  }

  @BeforeEach
  void BeforeEach() {
    crashTableManager = new CrashTableManager(crashFilterManager);
  }

  @Test
  public void initialPageNumber__ShouldBeZero() {
    assertEquals((Integer) 0, crashTableManager.getPageNumber());
  }

  @Test
  public void initialPageSize__ShouldBe200() {
    assertEquals((Integer) 200, crashTableManager.getPageSize());
  }

  @Test
  public void initialPageCount__ShouldBe52() {
    assertEquals((Integer) 52, crashTableManager.getPageCountProperty().getValue());
  }

  @Test
  public void initialTotalSize__ShouldBe10kCSV() {
    assertEquals((Integer) 10210, crashTableManager.getTotalSize());
  }

  @ParameterizedTest
  @ValueSource(ints = {5, 10, 20, 50, 100, 200, 1000})
  public void tablePageSize__ShouldBePageSize(Integer pageSize) {
    crashTableManager.setPageSize(pageSize);

    assertEquals(pageSize, crashTableManager.getTableDataSize());
  }

  // @ValueSource(ints = { 5, 10, 20, 50, 100, 200, 1000 })
  @ParameterizedTest
  @CsvSource({"5, 2042", "10, 1021", "20, 511", "50, 205", "100, 103", "200, 52", "1000, 11"})
  public void tablePageSize__ShouldAlterPageCount(Integer pageSize, Integer pageCount) {
    crashTableManager.setPageSize(pageSize);

    assertEquals(pageCount, crashTableManager.getPageCountProperty().getValue());
  }

  @Test
  public void setPageNumber__changesPageNumberAndData() {
    List<Crash> last = new ArrayList<>(crashTableManager.getTableData());

    crashTableManager.setPageNumber(1);
    assertEquals(1, crashTableManager.getPageNumber());
    assertNotEquals(last, crashTableManager.getTableData());
  }

  @Test
  public void checkOnLastAndOnFirstWork() {
    assertFalse(crashTableManager.onLastPage());
    assertTrue(crashTableManager.onFirstPage());

    crashTableManager.setPageNumber(crashTableManager.getPageCountProperty().get() - 1);

    assertTrue(crashTableManager.onLastPage());
    assertFalse(crashTableManager.onFirstPage());
  }

  @Test
  public void getColumns__ShouldNotIncludeId() {
    assertFalse(crashTableManager.getColumns().contains("id"));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 10, 20})
  public void setSort__ChangesTableDataOnDifferentPages(Integer page) {
    crashTableManager.setPageNumber(page);

    List<Crash> last = new ArrayList<>(crashTableManager.getTableData());
    crashTableManager.setSorts(Arrays.asList(new Pair<>("region", false)));
    assertNotEquals(last, crashTableManager.getTableData());
  }

  @ParameterizedTest
  @CsvSource({"200, 0, 1, 200", "0, 0, 0, 0", "300, 1, 301, 600"})
  public void getTableDataRows__isCorrectForPageSizeAndNumber(
      Integer pageSize, Integer pageNumber, Integer low, Integer high) {
    crashTableManager.setPageSize(pageSize);
    crashTableManager.setPageNumber(pageNumber);

    Pair<Integer, Integer> result = crashTableManager.getTableDataRows();

    assertEquals(low, result.getKey());
    assertEquals(high, result.getValue());
  }
}
