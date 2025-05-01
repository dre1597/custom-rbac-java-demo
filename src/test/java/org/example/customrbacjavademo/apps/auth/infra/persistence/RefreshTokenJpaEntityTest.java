package org.example.customrbacjavademo.apps.auth.infra.persistence;

import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RefreshTokenJpaEntityTest {
  @Test
  void shouldCreateRefreshToken() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();

    final var jpaEntity = new RefreshTokenJpaEntity(
        id,
        "token",
        now,
        now,
        now,
        new UserJpaEntity()
    );

    assertEquals(id, jpaEntity.getId());
    assertEquals("token", jpaEntity.getToken());
    assertEquals(now, jpaEntity.getExpiryDate());
    assertEquals(now, jpaEntity.getCreatedAt());
    assertEquals(now, jpaEntity.getUpdatedAt());
  }

  @Test
  void shouldSetAndGetFieldsCorrectly() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();
    final var userId = UUID.randomUUID();
    final var userJpa = new UserJpaEntity();
    userJpa.setId(userId);

    final var jpaEntity = new RefreshTokenJpaEntity();
    jpaEntity.setId(id);
    jpaEntity.setToken("token");
    jpaEntity.setExpiryDate(now);
    jpaEntity.setCreatedAt(now);
    jpaEntity.setUpdatedAt(now);
    jpaEntity.setUser(userJpa);

    assertEquals(id, jpaEntity.getId());
    assertEquals("token", jpaEntity.getToken());
    assertEquals(now, jpaEntity.getExpiryDate());
    assertEquals(now, jpaEntity.getCreatedAt());
    assertEquals(now, jpaEntity.getUpdatedAt());
    assertEquals(userJpa, jpaEntity.getUser());
  }

  @Test
  void shouldFormatToStringCorrectly() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();
    final var userId = UUID.randomUUID();
    final var userJpa = new UserJpaEntity();
    userJpa.setId(userId);

    final var jpaEntity = new RefreshTokenJpaEntity(
        id, "token", now, now, now, userJpa);

    final var expectedToString = String.format(
        "RefreshTokenJpaEntity{id=%s, token='token', expiryDate=%s, createdAt=%s, updatedAt=%s, user=%s}",
        id, now, now, now, userJpa
    );

    assertEquals(expectedToString, jpaEntity.toString());
  }
}
