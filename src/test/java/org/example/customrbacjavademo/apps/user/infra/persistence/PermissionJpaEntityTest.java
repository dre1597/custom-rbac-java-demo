package org.example.customrbacjavademo.apps.user.infra.persistence;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionJpaEntityTest {
  @Test
  void shouldCreatePermission() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();

    final var jpaEntity = new PermissionJpaEntity(
        id,
        "name",
        "scope",
        "description",
        "status",
        now,
        now
    );

    assertEquals(id, jpaEntity.getId());
    assertEquals("name", jpaEntity.getName());
    assertEquals("scope", jpaEntity.getScope());
    assertEquals("description", jpaEntity.getDescription());
    assertEquals("status", jpaEntity.getStatus());
    assertEquals(now, jpaEntity.getCreatedAt());
    assertEquals(now, jpaEntity.getUpdatedAt());
  }

  @Test
  void shouldSetAndGetFieldsCorrectly() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();

    final var jpaEntity = new PermissionJpaEntity();
    jpaEntity.setId(id);
    jpaEntity.setName("name");
    jpaEntity.setScope("scope");
    jpaEntity.setDescription("description");
    jpaEntity.setStatus("status");
    jpaEntity.setCreatedAt(now);
    jpaEntity.setUpdatedAt(now);

    assertEquals(id, jpaEntity.getId());
    assertEquals("name", jpaEntity.getName());
    assertEquals("scope", jpaEntity.getScope());
    assertEquals("description", jpaEntity.getDescription());
    assertEquals("status", jpaEntity.getStatus());
    assertEquals(now, jpaEntity.getCreatedAt());
    assertEquals(now, jpaEntity.getUpdatedAt());
  }
}
