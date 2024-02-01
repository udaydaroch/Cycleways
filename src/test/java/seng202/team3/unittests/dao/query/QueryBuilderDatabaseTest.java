package seng202.team3.unittests.dao.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import seng202.team3.Database;
import seng202.team3.dao.DAOInterface;
import seng202.team3.dao.query.QueryBuilder;
import seng202.team3.exceptions.InstanceAlreadyExistsException;

public class QueryBuilderDatabaseTest {
  @TempDir static Path tempDir;

  class TestModel {
    public Integer id;
    public String text;

    TestModel(Integer id, String text) {
      this.id = id;
      this.text = text;
    }
  }

  class TestModelDAO implements DAOInterface<TestModel> {

    @Override
    public Integer add(TestModel toAdd) {
      throw new UnsupportedOperationException("Unimplemented method 'add'");
    }

    @Override
    public boolean delete(TestModel toDelete) {
      throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void update(TestModel toUpdate) {
      throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public TestModel getOneFromResult(ResultSet resultSet) throws SQLException {
      return new TestModel(resultSet.getInt("id"), resultSet.getString("text"));
    }

    @Override
    public List<TestModel> getAll() {
      throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    public QueryBuilder<TestModel> query() {
      return new QueryBuilder<>(this);
    }

    @Override
    public String getTableName() {
      return "test";
    }
  }

  QueryBuilder<TestModel> queryBuilder;
  static Integer testSize = 1000;

  @BeforeAll
  static void beforeAll() throws InstanceAlreadyExistsException, SQLException {
    Database.RESET();
    Database db = Database.getDatabaseWithPath(tempDir.resolve("database.db").toString());

    Connection conn = db.connect();
    Statement statement = conn.createStatement();
    statement.executeUpdate("CREATE TABLE test (id INTEGER PRIMARY KEY, text TEXT);");

    List<String> values = new ArrayList<>();
    for (int i = 0; i < testSize; i++) {
      values.add("(%d, 'row %d')".formatted(i, i));
    }
    String sql = "INSERT INTO test (id, text) VALUES " + String.join(",", values) + ";";
    statement.executeUpdate(sql);
  }

  @BeforeEach
  void beforeEach() {
    TestModelDAO testModelDAO = new TestModelDAO();
    queryBuilder = testModelDAO.query();
  }

  @Test
  void getAll__returnsAllResults() {
    List<TestModel> results = queryBuilder.getAll();

    assertEquals(testSize, results.size());
  }

  @Test
  void getAllWithFilters__returnsOnlyResultsMatchingFilter() {
    List<TestModel> result =
        queryBuilder.filterIn("text", Arrays.asList("row 1", "row 3")).getAll();

    assertEquals(2, result.size());
    assertEquals(1, (result.get(0)).id);
    assertEquals(3, (result.get(1)).id);
  }

  @Test
  void getAllCount__returnsCorrectNumberOfResults() {
    Integer result = queryBuilder.getAllCount();

    assertEquals(testSize, result);
  }

  @Test
  void getAllCountWithFilters__returnsCorrectNumberOfResults() {
    Integer result = queryBuilder.filterIn("text", Arrays.asList("row 1", "row 3")).getAllCount();

    assertEquals(2, result);
  }

  @ParameterizedTest
  @CsvSource({"1000,1", "150,7", "1,1000", "10000,1"})
  void getPageCountSetPageSize__returnsCorrectNumberOfPages(Integer pageSize, Integer expected) {
    Integer result = queryBuilder.getPageCount(pageSize);

    assertEquals(expected, result);
  }

  @ParameterizedTest
  @CsvSource({"1000", "150", "1", "10000"})
  void getPageSetPageSize__returnsAllResults(Integer pageSize) {
    List<TestModel> result;
    List<TestModel> allResult = new ArrayList<>();

    int page = 0;
    do {
      result = queryBuilder.getPage(page, pageSize);
      page++;
      allResult.addAll(result);
    } while (!result.isEmpty());

    assertEquals(testSize, allResult.size());
  }

  @Test
  void getPageAsJSON__returnsAllResultsAsJSON() {
    List<String> firstThreeHundred =
        queryBuilder.getPageAsJSON(Arrays.asList("id", "text"), 0, 300);

    assertEquals("{\"id\":0,\"text\":\"row 0\"}", firstThreeHundred.get(0));
    assertEquals("{\"id\":20,\"text\":\"row 20\"}", firstThreeHundred.get(20));

    List<String> lastThreeHundred = queryBuilder.getPageAsJSON(Arrays.asList("id", "text"), 3, 300);

    assertEquals("{\"id\":900,\"text\":\"row 900\"}", lastThreeHundred.get(0));
  }
}
