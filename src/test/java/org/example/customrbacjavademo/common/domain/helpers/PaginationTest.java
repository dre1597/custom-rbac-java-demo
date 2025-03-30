package org.example.customrbacjavademo.common.domain.helpers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaginationTest {
  @Test
  void shouldMapItemsCorrectly() {
    var pagination = new Pagination<Integer>(1, 10, 100, List.of(1, 2, 3));
    Function<Integer, String> mapper = Object::toString;
    Pagination<String> mappedPagination = pagination.map(mapper);

    assertEquals(1, mappedPagination.currentPage());
    assertEquals(10, mappedPagination.perPage());
    assertEquals(100, mappedPagination.total());
    assertEquals(List.of("1", "2", "3"), mappedPagination.items());
  }

  @Test
  void shouldHandleEmptyItemsList() {
    var pagination = new Pagination<Integer>(1, 10, 0, List.of());
    Function<Integer, String> mapper = Object::toString;
    var mappedPagination = pagination.map(mapper);

    assertEquals(1, mappedPagination.currentPage());
    assertEquals(10, mappedPagination.perPage());
    assertEquals(0, mappedPagination.total());
    assertEquals(List.of(), mappedPagination.items());
  }
}
