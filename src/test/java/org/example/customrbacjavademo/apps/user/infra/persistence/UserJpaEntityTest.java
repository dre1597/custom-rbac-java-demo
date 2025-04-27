package org.example.customrbacjavademo.apps.user.infra.persistence;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
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

  @Test
  void shouldFormatToStringCorrectly() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();

    final var role = new RoleJpaEntity(
        UUID.randomUUID(),
        "ADMIN",
        "Admin role",
        "ACTIVE",
        now,
        now,
        List.of()
    );

    final var jpaEntity = new UserJpaEntity(
        id,
        "any_user",
        "any_password",
        "ACTIVE",
        now,
        now,
        role
    );

    final var expectedString = """
        UserJpaEntity{
            id=%s,
            name='any_user',
            status='ACTIVE',
            createdAt=%s,
            updatedAt=%s,
            role=%s
        }""".formatted(
        id, now, now, role.toString()
    );

    assertEquals(expectedString, jpaEntity.toString());
  }

  @Test
  void shouldFormatToStringCorrectlyWithNullRole() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();

    final var jpaEntity = new UserJpaEntity(
        id,
        "any_user",
        "any_password",
        "ACTIVE",
        now,
        now,
        null
    );

    final var expectedString = """
        UserJpaEntity{
            id=%s,
            name='any_user',
            status='ACTIVE',
            createdAt=%s,
            updatedAt=%s,
            role=null
        }""".formatted(
        id, now, now
    );

    assertEquals(expectedString, jpaEntity.toString());
  }
}
