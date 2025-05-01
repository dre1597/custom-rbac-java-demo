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

    assertNotNull(refreshToken.id());
    assertEquals(token, refreshToken.token());
    assertEquals(expiryDate, refreshToken.expiryDate());
    assertNotNull(refreshToken.createdAt());
    assertNotNull(refreshToken.updatedAt());
    assertEquals(userId, refreshToken.userId());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldNotCreateRefreshTokenWithInvalidToken(final String token) {
    final var expiryDate = Instant.now().plusSeconds(3600);
    final var userId = UUID.randomUUID();

    var exception = assertThrows(ValidationException.class, () -> RefreshToken.newRefreshToken(token, expiryDate, userId));

    assertTrue(exception.getMessage().contains("token is required"));
  }

  @Test
  void shouldNotCreateRefreshTokenWithInvalidExpiryDate() {
    final var token = "valid-token";
    final var userId = UUID.randomUUID();

    var exception = assertThrows(ValidationException.class, () -> RefreshToken.newRefreshToken(token, null, userId));

    assertTrue(exception.getMessage().contains("expiryDate is required"));
  }

  @Test
  void shouldNotCreateRefreshTokenWithInvalidUserId() {
    final var token = "valid-token";
    final var expiryDate = Instant.now().plusSeconds(3600);

    var exception = assertThrows(ValidationException.class, () -> RefreshToken.newRefreshToken(token, expiryDate, null));

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

    assertNotNull(refreshToken.id());
    assertEquals(token, refreshToken.token());
    assertEquals(expiryDate, refreshToken.expiryDate());
    assertEquals(createdAt, refreshToken.createdAt());
    assertEquals(updatedAt, refreshToken.updatedAt());
    assertEquals(userId, refreshToken.userId());
  }
}

