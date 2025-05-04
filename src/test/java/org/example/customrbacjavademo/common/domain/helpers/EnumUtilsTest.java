package org.example.customrbacjavademo.common.domain.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnumUtilsTest {
  @Test
  void shouldReturnAllEnumValuesAsCommaSeparatedString() {
    var result = EnumUtils.enumValuesAsString(DummyEnum.class);
    assertEquals("FOO, BAR, BAZ", result);
  }
}
