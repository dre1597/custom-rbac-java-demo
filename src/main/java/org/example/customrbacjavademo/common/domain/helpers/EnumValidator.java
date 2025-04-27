package org.example.customrbacjavademo.common.domain.helpers;

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
}
