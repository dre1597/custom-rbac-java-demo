package org.example.customrbacjavademo.apps.auth.domain.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {
  private JwtService service;
  private final long expirationTime = 1000 * 60 * 60;
  private final String secret = "ZmFrZXNlY3JldGtleWZha2VzZWNyZXRrZXlmYWtlc2VjcmV0a2V5ZmFrZQ==";


  @BeforeEach
  void setup() {
    service = new JwtService();
    ReflectionTestUtils.setField(service, "secretKey", secret);
    ReflectionTestUtils.setField(service, "jwtExpiration", expirationTime);
  }

  @Test
  void shouldExtractUsernameFromToken() {
    var username = "user";
    var token = createToken(username);

    var extractedUsername = service.extractUsername(token);

    assertEquals(username, extractedUsername);
  }

  @Test
  void shouldExtractCustomClaim() {
    var token = createToken("user");

    var expiration = service.extractClaim(token, Claims::getExpiration);

    assertNotNull(expiration);
  }

  @Test
  void shouldGenerateToken() {
    var userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("user");

    var token = service.generateToken(userDetails);

    assertNotNull(token);
    assertFalse(token.isEmpty());
  }

  @Test
  void shouldGenerateTokenWithExtraClaims() {
    var userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("user");
    Map<String, Object> extraClaims = Map.of("role", "ADMIN");

    var token = service.generateToken(extraClaims, userDetails);

    assertNotNull(token);
    assertFalse(token.isEmpty());
  }

  @Test
  void shouldReturnExpirationTime() {
    var expiration = service.getExpirationTime();
    assertEquals(expirationTime, expiration);
  }

  @Test
  void shouldValidateValidToken() {
    var userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("user");
    var token = createToken("user");

    var valid = service.isTokenValid(token, userDetails);

    assertTrue(valid);
  }

  @Test
  void shouldInvalidateTokenWithDifferentUsername() {
    var userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("otherUser");
    var token = createToken("user");

    var valid = service.isTokenValid(token, userDetails);

    assertFalse(valid);
  }

  @Test
  void shouldReturnFalseWhenTokenIsExpired() {
    var expiredToken = createExpiredToken("user");

    var userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("user");

    assertFalse(service.isTokenValid(expiredToken, userDetails));
  }

  @Test
  void shouldReturnTrueWhenValidTokenAndCorrectUser() {
    var userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("user");
    var validToken = createToken("user");

    assertTrue(service.isTokenValid(validToken, userDetails));
  }

  @Test
  void shouldReturnFalseWhenValidTokenButWrongUser() {
    var userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("wrongUser");
    var validToken = createToken("user");

    assertFalse(service.isTokenValid(validToken, userDetails));
  }

  @Test
  void shouldReturnTrueForValidTokenAndCorrectUser() {
    var userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("user");
    var validToken = createToken("user");

    assertTrue(service.isTokenValid(validToken, userDetails));
  }

  @Test
  void shouldReturnFalseForValidTokenButWrongUser() {
    var userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn("wrongUser");
    var validToken = createToken("user");

    assertFalse(service.isTokenValid(validToken, userDetails));
  }

  private String createToken(final String username) {
    var now = System.currentTimeMillis();
    var key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    return Jwts.builder()
        .subject(username)
        .issuedAt(new Date(now))
        .expiration(new Date(now + expirationTime))
        .signWith(key)
        .compact();
  }

  private String createExpiredToken(final String username) {
    var now = System.currentTimeMillis();
    var key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    return Jwts.builder()
        .subject(username)
        .issuedAt(new Date(now - 5000))
        .expiration(new Date(now - 3000))
        .signWith(key)
        .compact();
  }
}
