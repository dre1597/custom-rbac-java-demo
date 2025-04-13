package org.example.customrbacjavademo.common.domain.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

enum DummyEnum {
  FOO, BAR, BAZ
}

class EnumValidatorTest {
  @Test
  void shouldReturnFalseWhenEnumValueIsValid() {
    assertFalse(EnumValidator.isInvalidEnum("FOO", DummyEnum.class));
    assertFalse(EnumValidator.isInvalidEnum("BAR", DummyEnum.class));
  }

  @Test
  void shouldReturnTrueWhenEnumValueIsInvalid() {
    assertTrue(EnumValidator.isInvalidEnum("INVALID", DummyEnum.class));
    assertTrue(EnumValidator.isInvalidEnum("foo", DummyEnum.class));
    assertTrue(EnumValidator.isInvalidEnum(null, DummyEnum.class));
  }

  @Test
  void shouldReturnAllEnumValuesAsCommaSeparatedString() {
    var result = EnumValidator.enumValuesAsString(DummyEnum.class);
    assertEquals("FOO, BAR, BAZ", result);
  }
}
