package seng202.team3.unittests.dao.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import seng202.team3.dao.query.QueryBuilder;

public class QueryBuilderTest extends QueryBuilderStringTestBase {
  @Test
  void GetAllStringNoFiltersOrSorting__OnlySelectAndNoParameters() {
    QueryBuilder<Void> query = queryBuilder;
    assertEquals(selectAllPrefix + ";", queryBuilder.getAllString());
    assertEquals(Arrays.asList(), query.getParams());
  }

  @Test
  void GetAllCountStringNoFiltersOrSorting__OnlySelectCountAndNoParameters() {
    QueryBuilder<Void> query = queryBuilder;
    assertEquals(
        selectAllPrefix.replace("SELECT *", "SELECT COUNT(*)") + ";",
        queryBuilder.getAllCountString());
    assertEquals(Arrays.asList(), query.getParams());
  }

  @ParameterizedTest
  @CsvSource({
    "0,1000",
    "1,1000",
    "10,1000",
    "10,1000",
    "0,300000",
    "1,300000",
    "4,300000",
  })
  void GetPageString__LimitPageSizeAndOffsetIsPageSizeTimesPageNumber(
      Integer pageNumber, Integer pageSize) {
    QueryBuilder<Void> query = queryBuilder;
    assertEquals(
        selectAllPrefix + " LIMIT " + pageSize + " OFFSET " + (pageSize * pageNumber) + ";",
        query.getPageString(pageNumber, pageSize));
  }

  @Test
  void getPageAsJSONString__SelectJSONObjectWithColumns() {
    QueryBuilder<Void> query = queryBuilder;
    String sql = query.getPageAsJSONString(Arrays.asList("id", "x", "y"), 0, 1000);
    assertEquals(
        selectAllPrefix.replace("SELECT *", "SELECT JSON_OBJECT('id',id,'x',x,'y',y) as json")
            + " LIMIT 1000 OFFSET 0;",
        sql);
  }

  @Test
  void Clear__QueryIsReset() {
    QueryBuilder<Void> query = queryBuilder;

    // check query is empty
    assertEquals(selectAllPrefix + ";", query.getAllString());
    assertEquals(Arrays.asList(), query.getParams());

    // add some stuff to the query
    query = query.filterEqual("a", "a").sort("a", false);
    assertNotEquals(selectAllPrefix + ";", query.getAllString());
    assertNotEquals(Arrays.asList(), query.getParams());

    query.clear();

    // check query is again
    assertEquals(selectAllPrefix + ";", query.getAllString());
    assertEquals(Arrays.asList(), query.getParams());
  }

  @Test
  void CopyBaseQueryUsingClone__BaseQueryIsNotAlteredByCopy() {
    QueryBuilder<Void> base = queryBuilder.filterEqual("a", "a");
    QueryBuilder<Void> copy = base.clone();
    copy = copy.sort("b", false);

    assertEquals(selectAllPrefix + " WHERE a = ? ORDER BY b ASC NULLS LAST;", copy.getAllString());
    assertEquals(selectAllPrefix + " WHERE a = ?;", base.getAllString());
  }

  @Test
  void CopyBaseQueryWithoutClone__BaseQueryIsAlteredByCopy() {
    QueryBuilder<Void> base = queryBuilder.filterEqual("a", "a");
    QueryBuilder<Void> copy = base;
    copy = copy.sort("b", false);

    assertEquals(selectAllPrefix + " WHERE a = ? ORDER BY b ASC NULLS LAST;", copy.getAllString());
    assertEquals(selectAllPrefix + " WHERE a = ? ORDER BY b ASC NULLS LAST;", base.getAllString());
  }
}
