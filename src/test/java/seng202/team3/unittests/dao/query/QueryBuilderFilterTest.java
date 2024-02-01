package seng202.team3.unittests.dao.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections.ListUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import seng202.team3.dao.query.QueryBuilder;

public class QueryBuilderFilterTest extends QueryBuilderStringTestBase {
  @Test
  void NoFilters_NoWhere() {
    String result = queryBuilder.getAllString();
    assertEquals(selectAllPrefix + ";", result);
  }

  @Test
  void FilterBetweenNumbers__BetweenInWhere() {
    QueryBuilder<Void> query = queryBuilder.filterBetween("year", 2001, 2011);
    assertEquals(selectAllPrefix + " WHERE year BETWEEN ? AND ?;", query.getAllString());
    assertEquals(Arrays.asList("2001", "2011"), query.getParams());
  }

  @Test
  void FilterNonZero__ColumnGreaterThanZeroInWhere() {
    QueryBuilder<Void> query = queryBuilder.filterNonZero("bycicle");
    assertEquals(selectAllPrefix + " WHERE bycicle > 0;", query.getAllString());
  }

  @Test
  void FilterNonZeroColumnList__AllColumnsGreaterThanZeroSeperatedByAndInWhere() {
    QueryBuilder<Void> query = queryBuilder.filterNonZero(Arrays.asList("bycicle", "car"));
    assertEquals(selectAllPrefix + " WHERE bycicle > 0 AND car > 0;", query.getAllString());
  }

  @Nested
  class FilterEqual {
    @Test
    void String__EqualsParameterInWhereAndParametersEqualToValue() {
      QueryBuilder<Void> query = queryBuilder.filterEqual("car", "blue");
      assertEquals(selectAllPrefix + " WHERE car = ?;", query.getAllString());
      assertEquals(Arrays.asList("blue"), query.getParams());
    }

    @Test
    void Integer__EqualsParameterInWhereAndParametersEqualToStringOfValue() {
      Integer value = 69;
      QueryBuilder<Void> query = queryBuilder.filterEqual("car", value);
      assertEquals(selectAllPrefix + " WHERE car = ?;", query.getAllString());
      assertEquals(Arrays.asList(value.toString()), query.getParams());
    }

    @Test
    void Float__EqualsParameterInWhereAndParametersEqualToStringOfValue() {
      Float value = 4.20f;
      QueryBuilder<Void> query = queryBuilder.filterEqual("car", value);
      assertEquals(selectAllPrefix + " WHERE car = ?;", query.getAllString());
      assertEquals(Arrays.asList(value.toString()), query.getParams());
    }
  }

  @Nested
  class FilterIn {
    @Test
    void OnlyOneValue__ColumnInValueInWhere() {
      List<String> values = Arrays.asList("rain");
      QueryBuilder<Void> query = queryBuilder.filterIn("weather", values);
      assertEquals(selectAllPrefix + " WHERE weather IN (?);", query.getAllString());
      assertEquals(values, query.getParams());
    }

    @Test
    void __ColumnInValuesInWhere() {
      List<String> values = Arrays.asList("rain", "sun");
      QueryBuilder<Void> query = queryBuilder.filterIn("weather", values);
      assertEquals(selectAllPrefix + " WHERE weather IN (?, ?);", query.getAllString());
      assertEquals(values, query.getParams());
    }

    @Test
    void Empty__TrueInWhere() {
      List<String> values = Arrays.asList(); // nothing
      QueryBuilder<Void> query = queryBuilder.filterIn("weather", values);
      assertEquals(selectAllPrefix + " WHERE TRUE;", query.getAllString());
      assertEquals(values, query.getParams());
    }

    @Test
    void Null__TrueInWhere() {
      List<String> values = null;
      QueryBuilder<Void> query = queryBuilder.filterIn("weather", values);
      assertEquals(selectAllPrefix + " WHERE TRUE;", query.getAllString());
      assertEquals(Arrays.asList(), query.getParams());
    }
  }

  @Nested
  class FilterInOrOther {
    List<String> values = Arrays.asList("rain", "sun");
    List<String> allValues = Arrays.asList("rain", "sun", "hail", "snow");

    @Test
    void False__SameAsFilterIn() {
      QueryBuilder<Void> queryFilterIn = queryBuilder.filterIn("weather", values);
      QueryBuilder<Void> queryFilterInOrOther =
          queryBuilder.filterInOrOther("weather", values, allValues, false);
      assertEquals(queryFilterIn.getAllString(), queryFilterInOrOther.getAllString());
      assertEquals(queryFilterIn.getParams(), queryFilterInOrOther.getParams());
    }

    @Test
    void True__ColumnInValuesOrColumnNotInKnowValues() {
      QueryBuilder<Void> queryFilterInOrOther =
          queryBuilder.filterInOrOther("weather", values, allValues, true);
      assertEquals(
          selectAllPrefix + " WHERE (weather IN (?, ?) OR weather NOT IN (?, ?, ?, ?));",
          queryFilterInOrOther.getAllString());
      assertEquals(ListUtils.union(values, allValues), queryFilterInOrOther.getParams());
    }
  }

  @Test
  void FilterNotIn____ColumnNotInValuesInWhere() {
    List<String> values = Arrays.asList("rain", "sun");
    QueryBuilder<Void> query = queryBuilder.filterIn("weather", values, true);
    assertEquals(selectAllPrefix + " WHERE weather NOT IN (?, ?);", query.getAllString());
    assertEquals(values, query.getParams());
  }

  @Nested
  class FilterInColumns {
    @Test
    void Empty__FalseInWhere() {
      List<String> values = Arrays.asList(); // nothing
      QueryBuilder<Void> query = queryBuilder.filterInColumns("ilam road", values);
      assertEquals(selectAllPrefix + " WHERE FALSE;", query.getAllString());
      assertEquals(values, query.getParams());
    }

    @Test
    void Null__FalseInWhere() {
      List<String> values = null;
      QueryBuilder<Void> query = queryBuilder.filterInColumns("ilam road", values);
      assertEquals(selectAllPrefix + " WHERE FALSE;", query.getAllString());
      assertEquals(Arrays.asList(), query.getParams());
    }

    @Test
    void OnlyOneColumn__ColumnLikeValueInWhere() {
      QueryBuilder<Void> query = queryBuilder.filterInColumns("ilam road", Arrays.asList("road1"));
      assertEquals(selectAllPrefix + " WHERE ((road1 LIKE ?));", query.getAllString());
      assertEquals(Arrays.asList("ilam road%"), query.getParams());
    }

    @Test
    void __ColumnLikeValueForEachColumnSeperatedByOrInWhere() {
      QueryBuilder<Void> query =
          queryBuilder.filterInColumns("ilam road", Arrays.asList("road1", "road2"));
      assertEquals(
          selectAllPrefix + " WHERE ((road1 LIKE ?) OR (road2 LIKE ?));", query.getAllString());
      assertEquals(Arrays.asList("ilam road%", "ilam road%"), query.getParams());
    }
  }

  @Test
  void MutipleFilters__JoinedWithAndInWhere() {
    QueryBuilder<Void> query = queryBuilder.filterEqual("a", "a").filterEqual("b", "b");
    assertEquals(selectAllPrefix + " WHERE a = ? AND b = ?;", query.getAllString());
    assertEquals(Arrays.asList("a", "b"), query.getParams());
  }
}
