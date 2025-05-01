package org.example.customrbacjavademo.apps.auth.domain.mocks;

import org.example.customrbacjavademo.apps.auth.domain.entities.RefreshToken;

import java.time.Instant;
import java.util.UUID;

public class RefreshTokenTestMocks {
  public static RefreshToken createActiveTestRefreshToken(final String token, final UUID userId) {
    return RefreshToken.newRefreshToken(token, Instant.now().plusSeconds(3600), userId);
  }
}
