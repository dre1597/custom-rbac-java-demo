package org.example.customrbacjavademo.common.domain.helpers;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class EnumUtils {
  private EnumUtils() {
  }

  public static <E extends Enum<E>> String enumValuesAsString(Class<E> enumType) {
    return Stream.of(enumType.getEnumConstants())
        .map(Enum::name)
        .collect(Collectors.joining(", "));
  }
}
