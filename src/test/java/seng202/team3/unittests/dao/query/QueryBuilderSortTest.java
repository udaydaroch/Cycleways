package seng202.team3.unittests.dao.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import seng202.team3.dao.query.QueryBuilder;

public class QueryBuilderSortTest extends QueryBuilderStringTestBase {
  @Test
  void SortColumn__ColumnAscInOrderBy() {
    QueryBuilder<Void> query = queryBuilder.sort("column", false);
    assertEquals(selectAllPrefix + " ORDER BY column ASC NULLS LAST;", query.getAllString());
    // sanity check that no params are added when sorting
    assertEquals(Arrays.asList(), query.getParams());
  }

  @Test
  void SortColumnReverseTrue__ColumnDescInOrderBy() {
    QueryBuilder<Void> query = queryBuilder.sort("column", true);
    assertEquals(selectAllPrefix + " ORDER BY column DESC;", query.getAllString());
  }

  @Nested
  class SortColumns {
    @Test
    void AllReverseFalse__EveryColumnAscJoinedWithCommaInOrderBy() {
      QueryBuilder<Void> query =
          queryBuilder.sort("column1", false).sort("column2", false).sort("column3", false);
      assertEquals(
          selectAllPrefix
              + " ORDER BY column1 ASC NULLS LAST, column2 ASC NULLS LAST, column3 ASC NULLS LAST;",
          query.getAllString());
    }

    @Test
    void AllReverseTrue__EveryColumnDescJoinedWithCommaInOrderBy() {
      QueryBuilder<Void> query =
          queryBuilder.sort("column1", true).sort("column2", true).sort("column3", true);
      assertEquals(
          selectAllPrefix + " ORDER BY column1 DESC, column2 DESC, column3 DESC;",
          query.getAllString());
    }

    @Test
    void TwoColumnsSecondReverseTrue__ColumnAscThenColumnDesc() {
      QueryBuilder<Void> query = queryBuilder.sort("column1", false).sort("column2", true);
      assertEquals(
          selectAllPrefix + " ORDER BY column1 ASC NULLS LAST, column2 DESC;",
          query.getAllString());
    }
  }
}
