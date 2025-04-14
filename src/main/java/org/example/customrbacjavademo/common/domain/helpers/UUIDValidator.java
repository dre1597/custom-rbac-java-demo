package org.example.customrbacjavademo.common.domain.helpers;

import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;

import java.util.UUID;

public final class UUIDValidator {

  private UUIDValidator() {
  }

  public static UUID parseOrThrow(final String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException ex) {
      throw new ValidationException("Invalid UUID: " + id);
    }
  }
}
