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
    final var prop = "name";
    final var term = "test";
    final var spec = SpecificationUtils.like(prop, term);

    final var root = mock(Root.class);
    final var query = mock(CriteriaQuery.class);
    final var cb = mock(CriteriaBuilder.class);
    final var expression = mock(Expression.class);
    final var predicate = mock(Predicate.class);

    when(cb.upper(root.get(prop))).thenReturn(expression);
    when(cb.like(expression, "%" + term.toUpperCase() + "%")).thenReturn(predicate);

    final var result = spec.toPredicate(root, query, cb);
    assertEquals(result, predicate);
  }

  @Test
  void shouldCreateLikeMultipleSpecification() {
    final var props = List.of("name", "description");
    final var term = "test";
    final var spec = SpecificationUtils.likeMultiple(props, term);

    final var root = mock(Root.class);
    final var query = mock(CriteriaQuery.class);
    final var cb = mock(CriteriaBuilder.class);
    final var expressionName = mock(Expression.class);
    final var expressionDescription = mock(Expression.class);
    final var predicate = mock(Predicate.class);

    when(cb.upper(root.get("name"))).thenReturn(expressionName);
    when(cb.upper(root.get("description"))).thenReturn(expressionDescription);
    when(cb.like(expressionName, "%" + term.toUpperCase() + "%")).thenReturn(predicate);
    when(cb.like(expressionDescription, "%" + term.toUpperCase() + "%")).thenReturn(predicate);
    when(cb.or(any(Predicate[].class))).thenReturn(predicate);

    final var result = spec.toPredicate(root, query, cb);
    assertEquals(result, predicate);
  }
}
