package org.example.customrbacjavademo.common.domain.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchQueryTest {
  @Test
  void shouldCreateSearchQueryWithGivenValues() {
    var query = new SearchQuery(1, 10, "test", "name", "asc");

    assertEquals(1, query.page());
    assertEquals(10, query.perPage());
    assertEquals("test", query.terms());
    assertEquals("name", query.sort());
    assertEquals("asc", query.direction());
  }

  @Test
  void shouldCreateSearchQueryWithDefaultValues() {
    var query = new SearchQuery(0, 0, "", "", "");

    assertEquals(0, query.page());
    assertEquals(0, query.perPage());
    assertEquals("", query.terms());
    assertEquals("", query.sort());
    assertEquals("", query.direction());
  }
}
