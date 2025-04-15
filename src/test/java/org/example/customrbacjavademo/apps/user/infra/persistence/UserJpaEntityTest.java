package org.example.customrbacjavademo.apps.user.infra.persistence;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserJpaEntityTest {
  @Test
  void shouldCreateUser() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();

    final var jpaEntity = new UserJpaEntity(
        id,
        "name",
        "password",
        "status",
        now,
        now,
        null
    );

    assertEquals(id, jpaEntity.getId());
    assertEquals("name", jpaEntity.getName());
    assertEquals("password", jpaEntity.getPassword());
    assertEquals("status", jpaEntity.getStatus());
    assertEquals(now, jpaEntity.getCreatedAt());
    assertEquals(now, jpaEntity.getUpdatedAt());
    assertNull(jpaEntity.getRole());
  }

  @Test
  void shouldSetAndGetFieldsCorrectly() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();

    final var jpaEntity = new UserJpaEntity();
    jpaEntity.setId(id);
    jpaEntity.setName("name");
    jpaEntity.setStatus("status");
    jpaEntity.setCreatedAt(now);
    jpaEntity.setUpdatedAt(now);
    jpaEntity.setRole(null);

    assertEquals(id, jpaEntity.getId());
    assertEquals("name", jpaEntity.getName());
    assertEquals("status", jpaEntity.getStatus());
    assertEquals(now, jpaEntity.getCreatedAt());
    assertEquals(now, jpaEntity.getUpdatedAt());
    assertNull(jpaEntity.getRole());
  }
}
