package org.example.customrbacjavademo.apps.auth.domain.entities;

import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public record RefreshToken(
    UUID id,
    String token,
    Instant expiryDate,
    Instant createdAt,
    Instant updatedAt,
    UUID userId
) {
  public RefreshToken {
    validate(token, expiryDate, userId);

    if (id == null) {
      id = UUID.randomUUID();
    }
    if (createdAt == null) {
      createdAt = Instant.now();
    }
    if (updatedAt == null) {
      updatedAt = Instant.now();
    }
  }

  public RefreshToken(String token, Instant expiryDate, UUID userId) {
    this(null, token, expiryDate, null, null, userId);
  }

  private void validate(String token, Instant expiryDate, UUID userId) {
    final var errors = new ArrayList<String>();

    if (token == null || token.isBlank()) {
      errors.add("token is required");
    }

    if (expiryDate == null) {
      errors.add("expiryDate is required");
    }

    if (userId == null) {
      errors.add("userId is required");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  public static RefreshToken with(
      final UUID id,
      final String token,
      final Instant expiryDate,
      final Instant createdAt,
      final Instant updatedAt,
      final UUID userId
  ) {
    return new RefreshToken(id, token, expiryDate, createdAt, updatedAt, userId);
  }

  public static RefreshToken newRefreshToken(
      final String token,
      final Instant expiryDate,
      final UUID userId
  ) {
    return new RefreshToken(token, expiryDate, userId);
  }
}
