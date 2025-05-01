package org.example.customrbacjavademo.apps.auth.domain.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
  @InjectMocks
  private RefreshTokenService refreshTokenService;

  @Test
  void shouldGenerateToken() {
    final var token = refreshTokenService.generateToken();

    assertNotNull(token);
    assertEquals(12, token.length());
  }

  @Test
  void shouldReturnTrueWhenTokenIsNotExpired() {
    final var futureDate = Instant.now().plusSeconds(60);
    final var isExpired = refreshTokenService.isTokenExpired(futureDate);

    assertTrue(isExpired);
  }

  @Test
  void shouldReturnFalseWhenTokenIsExpired() {
    final var pastDate = Instant.now().minusSeconds(60);
    final var isExpired = refreshTokenService.isTokenExpired(pastDate);

    assertFalse(isExpired);
  }
}
