package org.example.customrbacjavademo.common.domain.utils;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class SpecificationUtils {
  private SpecificationUtils() {
  }

  public static <T> Specification<T> like(final String prop, final String term) {
    return (root, query, cb) -> cb.like(cb.upper(root.get(prop)), like(term.toUpperCase()));
  }

  public static <T> Specification<T> likeMultiple(final List<String> props, final String term) {
    return (root, query, cb) -> {
      var pattern = like(term.toUpperCase());
      var predicates = new ArrayList<Predicate>();
      for (var prop : props) {
        predicates.add(cb.like(cb.upper(root.get(prop)), pattern));
      }
      return cb.or(predicates.toArray(new Predicate[0]));
    };
  }

  private static String like(final String term) {
    return "%" + term + "%";
  }
}
