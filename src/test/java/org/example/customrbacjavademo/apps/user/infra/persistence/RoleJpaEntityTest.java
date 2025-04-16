package org.example.customrbacjavademo.apps.user.infra.persistence;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleJpaEntityTest {
  @Test
  void shouldCreateRole() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();

    final var jpaEntity = new RoleJpaEntity(
        id,
        "name",
        "description",
        "status",
        now,
        now,
        List.of()
    );

    assertEquals(id, jpaEntity.getId());
    assertEquals("name", jpaEntity.getName());
    assertEquals("description", jpaEntity.getDescription());
    assertEquals("status", jpaEntity.getStatus());
    assertEquals(now, jpaEntity.getCreatedAt());
    assertEquals(now, jpaEntity.getUpdatedAt());
    assertEquals(List.of(), jpaEntity.getPermissions());
  }

  @Test
  void shouldSetAndGetFieldsCorrectly() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();

    final var jpaEntity = new RoleJpaEntity();
    jpaEntity.setId(id);
    jpaEntity.setName("name");
    jpaEntity.setDescription("description");
    jpaEntity.setStatus("status");
    jpaEntity.setCreatedAt(now);
    jpaEntity.setUpdatedAt(now);
    jpaEntity.setPermissions(List.of());

    assertEquals(id, jpaEntity.getId());
    assertEquals("name", jpaEntity.getName());
    assertEquals("description", jpaEntity.getDescription());
    assertEquals("status", jpaEntity.getStatus());
    assertEquals(now, jpaEntity.getCreatedAt());
    assertEquals(now, jpaEntity.getUpdatedAt());
    assertEquals(List.of(), jpaEntity.getPermissions());
  }
}
