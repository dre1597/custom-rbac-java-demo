package org.example.customrbacjavademo.apps.auth.domain.services;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
  public String generateToken() {
    return UUID.randomUUID().toString();
  }

  public boolean isTokenExpired(final Instant expiryDate) {
    return !Instant.now().isAfter(expiryDate);
  }
}
