package org.example.customrbacjavademo.apps.auth.domain.services;

import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
  @InjectMocks
  private JwtService jwtService;

  private final String secretKey = "ZmFrZXNlY3JldGtleWZha2VzZWNyZXRrZXlmYWtlc2VjcmV0a2V5ZmFrZQ==";
  private final long expirationTime = 100000;

  @Test
  void shouldExtractUsernameFromToken() {
    ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", expirationTime);

    final var userDetails = createUserDetails();
    final var token = jwtService.generateToken(userDetails);

    final var username = jwtService.extractUsername(token);

    assertEquals(userDetails.getUsername(), username);
  }

  @Test
  void shouldExtractRoleIdFromToken() {
    ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", expirationTime);

    final var user = createUserJpaEntity();
    final var token = jwtService.generateToken(user);

    final var roleId = jwtService.extractRoleId(token);

    assertEquals(user.getRole().getId().toString(), roleId);
  }

  @Test
  void shouldGenerateValidTokenForUserDetails() {
    ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", expirationTime);

    final var userDetails = createUserDetails();
    final var token = jwtService.generateToken(userDetails);

    assertNotNull(token);
    assertTrue(jwtService.isTokenValid(token, userDetails));
  }

  @Test
  void shouldGenerateValidTokenForUserJpaEntity() {
    ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", expirationTime);

    final var user = createUserJpaEntity();
    final var token = jwtService.generateToken(user);

    assertNotNull(token);
    assertTrue(jwtService.isTokenValid(token, user));
  }

  @Test
  void shouldReturnFalseWhenTokenHasDifferentUser() {
    ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", expirationTime);

    final var userDetails = createUserDetails();
    final var otherUserDetails = User.withUsername("other_name")
        .password("any_password")
        .authorities(Collections.emptyList())
        .build();
    final var token = jwtService.generateToken(userDetails);

    assertFalse(jwtService.isTokenValid(token, otherUserDetails));
  }

  @Test
  void shouldReturnFalseWhenTokenIsExpired() {
    ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000);

    final var userDetails = createUserDetails();
    final var token = jwtService.generateToken(userDetails);

    assertFalse(jwtService.isTokenValid(token, userDetails));
  }

  @Test
  void shouldReturnFalseWhenTokenIsInvalid() {
    ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", expirationTime);

    final var invalidToken = "invalid.token.string";
    final var userDetails = createUserDetails();

    assertFalse(jwtService.isTokenValid(invalidToken, userDetails));
  }


  private UserDetails createUserDetails() {
    return User.withUsername("any_name")
        .password("any_password")
        .authorities(Collections.emptyList())
        .build();
  }

  private UserJpaEntity createUserJpaEntity() {
    final var role = new RoleJpaEntity(
        UUID.randomUUID(),
        "any_role",
        "any_description",
        "ACTIVE",
        Instant.now(),
        Instant.now(),
        List.of()
    );

    return new UserJpaEntity(
        UUID.randomUUID(),
        "any_name",
        "any_password",
        "ACTIVE",
        Instant.now(),
        Instant.now(),
        role
    );
  }
}
