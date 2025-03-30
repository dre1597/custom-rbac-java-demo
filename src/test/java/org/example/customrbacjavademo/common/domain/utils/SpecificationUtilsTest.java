package org.example.customrbacjavademo.common.domain.utils;

import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpecificationUtilsTest {
  @Test
  void shouldCreateLikeSpecification() {
    var prop = "name";
    var term = "test";
    var spec = SpecificationUtils.like(prop, term);

    var root = mock(Root.class);
    var query = mock(CriteriaQuery.class);
    var cb = mock(CriteriaBuilder.class);
    var expression = mock(Expression.class);
    var predicate = mock(Predicate.class);

    when(cb.upper(root.get(prop))).thenReturn(expression);
    when(cb.like(expression, "%" + term.toUpperCase() + "%")).thenReturn(predicate);

    var result = spec.toPredicate(root, query, cb);
    assertEquals(result, predicate);
  }

  @Test
  void shouldCreateLikeMultipleSpecification() {
    var props = List.of("name", "description");
    var term = "test";
    var spec = SpecificationUtils.likeMultiple(props, term);

    var root = mock(Root.class);
    var query = mock(CriteriaQuery.class);
    var cb = mock(CriteriaBuilder.class);
    var expressionName = mock(Expression.class);
    var expressionDescription = mock(Expression.class);
    var predicate = mock(Predicate.class);

    when(cb.upper(root.get("name"))).thenReturn(expressionName);
    when(cb.upper(root.get("description"))).thenReturn(expressionDescription);
    when(cb.like(expressionName, "%" + term.toUpperCase() + "%")).thenReturn(predicate);
    when(cb.like(expressionDescription, "%" + term.toUpperCase() + "%")).thenReturn(predicate);
    when(cb.or(any(Predicate[].class))).thenReturn(predicate);

    var result = spec.toPredicate(root, query, cb);
    assertEquals(result, predicate);
  }

  private static class MyEntity {
  }
}
