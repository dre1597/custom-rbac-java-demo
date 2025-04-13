package org.example.customrbacjavademo.common.domain.helpers;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class EnumValidator {
  private EnumValidator() {
  }

  public static <E extends Enum<E>> boolean isInvalidEnum(String value, Class<E> enumType) {
    if (value == null) return true;
    try {
      Enum.valueOf(enumType, value);
      return false;
    } catch (IllegalArgumentException e) {
      return true;
    }
  }

  public static <E extends Enum<E>> String enumValuesAsString(Class<E> enumType) {
    return Stream.of(enumType.getEnumConstants())
        .map(Enum::name)
        .collect(Collectors.joining(", "));
  }
}
