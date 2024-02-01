package seng202.team3.unittests.dao.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import seng202.team3.dao.DAOInterface;
import seng202.team3.dao.query.QueryBuilder;

public abstract class QueryBuilderStringTestBase {
  QueryBuilder<Void> queryBuilder;
  String tableName = "testTable";
  String selectAllPrefix = "SELECT * FROM %s".formatted(tableName);

  class TestTableDAO implements DAOInterface<Void> {

    @Override
    public Integer add(Void toAdd) {
      throw new UnsupportedOperationException("Unimplemented method 'add'");
    }

    @Override
    public boolean delete(Void toDelete) {

      throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void update(Void toUpdate) {
      throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Void getOneFromResult(ResultSet resultSet) throws SQLException {
      throw new UnsupportedOperationException("Unimplemented method 'getOneFromResult'");
    }

    @Override
    public List<Void> getAll() {
      throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    public QueryBuilder<Void> query() {
      return new QueryBuilder<>(this);
    }

    @Override
    public String getTableName() {
      return "testTable";
    }
  }

  private TestTableDAO testTableDAO = new TestTableDAO();

  @BeforeEach
  void beforeEach() {
    // get a new query for each test
    queryBuilder = testTableDAO.query();
  }
}
