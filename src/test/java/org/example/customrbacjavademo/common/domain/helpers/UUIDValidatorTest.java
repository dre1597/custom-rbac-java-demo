package org.example.customrbacjavademo.common.domain.helpers;

import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UUIDValidatorTest {
  @Test
  void shouldParseUUIDIfItIsValid() {
    final var uuid = UUID.randomUUID();

    assertEquals(uuid, UUIDValidator.parseOrThrow(uuid.toString()));
  }

  @Test
  void shouldThrowExceptionIfUUIDIsInvalid() {
    assertThrows(ValidationException.class, () -> UUIDValidator.parseOrThrow("invalid-uuid"));
  }
}
