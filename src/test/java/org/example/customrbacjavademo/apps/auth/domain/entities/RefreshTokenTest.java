package org.example.customrbacjavademo.apps.auth.domain.entities;

import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenTest {
  @Test
  void shouldCreateRefreshToken() {
    final var token = "valid-token";
    final var expiryDate = Instant.now().plusSeconds(3600); // Expira em 1 hora
    final var userId = UUID.randomUUID();

    final var refreshToken = RefreshToken.newRefreshToken(token, expiryDate, userId);

    assertNotNull(refreshToken.getId());
    assertEquals(token, refreshToken.getToken());
    assertEquals(expiryDate, refreshToken.getExpiryDate());
    assertNotNull(refreshToken.getCreatedAt());
    assertNotNull(refreshToken.getUpdatedAt());
    assertEquals(userId, refreshToken.getUserId());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldNotCreateRefreshTokenWithInvalidToken(final String token) {
    final var expiryDate = Instant.now().plusSeconds(3600);
    final var userId = UUID.randomUUID();

    var exception = assertThrows(ValidationException.class, () -> {
      RefreshToken.newRefreshToken(token, expiryDate, userId);
    });

    assertTrue(exception.getMessage().contains("token is required"));
  }

  @Test
  void shouldNotCreateRefreshTokenWithInvalidExpiryDate() {
    final var token = "valid-token";
    final var userId = UUID.randomUUID();

    var exception = assertThrows(ValidationException.class, () -> {
      RefreshToken.newRefreshToken(token, null, userId);
    });

    assertTrue(exception.getMessage().contains("expiryDate is required"));
  }

  @Test
  void shouldNotCreateRefreshTokenWithInvalidUserId() {
    final var token = "valid-token";
    final var expiryDate = Instant.now().plusSeconds(3600);

    var exception = assertThrows(ValidationException.class, () -> {
      RefreshToken.newRefreshToken(token, expiryDate, null);
    });

    assertTrue(exception.getMessage().contains("userId is required"));
  }

  @Test
  void shouldCreateRefreshTokenWithAllFields() {
    final var token = "valid-token";
    final var expiryDate = Instant.now().plusSeconds(3600);
    final var userId = UUID.randomUUID();
    final var createdAt = Instant.now();
    final var updatedAt = Instant.now();

    final var refreshToken = RefreshToken.with(
        UUID.randomUUID(),
        token,
        expiryDate,
        createdAt,
        updatedAt,
        userId
    );

    assertNotNull(refreshToken.getId());
    assertEquals(token, refreshToken.getToken());
    assertEquals(expiryDate, refreshToken.getExpiryDate());
    assertEquals(createdAt, refreshToken.getCreatedAt());
    assertEquals(updatedAt, refreshToken.getUpdatedAt());
    assertEquals(userId, refreshToken.getUserId());
  }

  @Test
  void shouldReturnCorrectStringRepresentation() {
    final var token = "valid-token";
    final var expiryDate = Instant.now().plusSeconds(3600);
    final var userId = UUID.randomUUID();
    final var refreshToken = RefreshToken.newRefreshToken(token, expiryDate, userId);

    final var expectedToString = "RefreshToken{id=" + refreshToken.getId() +
        ", token='" + token + '\'' +
        ", expiryDate=" + expiryDate +
        ", createdAt=" + refreshToken.getCreatedAt() +
        ", updatedAt=" + refreshToken.getUpdatedAt() +
        ", userId=" + userId +
        '}';

    assertEquals(expectedToString, refreshToken.toString());
  }
}

