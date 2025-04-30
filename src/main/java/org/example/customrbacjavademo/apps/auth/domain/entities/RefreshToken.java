package org.example.customrbacjavademo.apps.auth.domain.entities;

import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class RefreshToken {
  private UUID id = UUID.randomUUID();
  private final String token;
  private final Instant expiryDate;
  private Instant createdAt = Instant.now();
  private Instant updatedAt = Instant.now();
  private final UUID userId;

  public RefreshToken(
      final UUID id,
      final String token,
      final Instant expiryDate,
      final Instant createdAt,
      final Instant updatedAt,
      final UUID userId
  ) {
    this.id = id;
    this.token = token;
    this.expiryDate = expiryDate;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.userId = userId;
  }

  private RefreshToken(
      final String token,
      final Instant expiryDate,
      final UUID userId
  ) {
    this.validate(token, expiryDate, userId);
    this.token = token;
    this.expiryDate = expiryDate;
    this.userId = userId;
  }

  public static RefreshToken with(
      final UUID id,
      final String token,
      final Instant expiryDate,
      final Instant createdAt,
      final Instant updatedAt,
      final UUID userId
  ) {
    return new RefreshToken(
        id,
        token,
        expiryDate,
        createdAt,
        updatedAt,
        userId
    );
  }

  public static RefreshToken newRefreshToken(
      final String token,
      final Instant expiryDate,
      final UUID userId
  ) {
    return new RefreshToken(token, expiryDate, userId);
  }

  private void validate(
      final String token,
      final Instant expiryDate,
      final UUID userId
  ) {
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

  public UUID getId() {
    return this.id;
  }

  public String getToken() {
    return this.token;
  }

  public Instant getExpiryDate() {
    return expiryDate;
  }

  public Instant getCreatedAt() {
    return this.createdAt;
  }

  public Instant getUpdatedAt() {
    return this.updatedAt;
  }

  public UUID getUserId() {
    return this.userId;
  }

  @Override
  public String toString() {
    return "RefreshToken{" +
        "id=" + id +
        ", token='" + token + '\'' +
        ", expiryDate=" + expiryDate +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", userId=" + userId +
        '}';
  }
}
