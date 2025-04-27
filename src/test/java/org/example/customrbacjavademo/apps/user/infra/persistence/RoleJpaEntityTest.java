package org.example.customrbacjavademo.apps.user.infra.persistence;

import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

  @Test
  void shouldFormatToStringCorrectly() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();
    final var permission = new PermissionJpaEntity(
        UUID.randomUUID(),
        "READ",
        "USER",
        "Allows reading users",
        "ACTIVE",
        now,
        now
    );

    final var jpaEntity = new RoleJpaEntity(
        id,
        "ADMIN",
        "Role with all permissions",
        "ACTIVE",
        now,
        now,
        List.of(permission)
    );

    final var expectedString = """
        RoleJpaEntity{
            id=%s,
            name='ADMIN',
            description='Role with all permissions',
            status='ACTIVE',
            createdAt=%s,
            updatedAt=%s,
            permissions=%s
        }""".formatted(
        id, now, now, permission
    );

    assertEquals(expectedString, jpaEntity.toString());
  }

  @Test
  void shouldFormatToStringCorrectlyWithNullPermissions() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();

    final var jpaEntity = new RoleJpaEntity(
        id,
        "ADMIN",
        "Role with all permissions",
        "ACTIVE",
        now,
        now,
        null
    );

    final var expectedString = """
        RoleJpaEntity{
            id=%s,
            name='ADMIN',
            description='Role with all permissions',
            status='ACTIVE',
            createdAt=%s,
            updatedAt=%s,
            permissions=null
        }""".formatted(
        id, now, now
    );

    assertEquals(expectedString, jpaEntity.toString());
  }

  @Test
  void shouldFormatToStringCorrectlyWithNullPermissionInList() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();

    final var permissions = new ArrayList<PermissionJpaEntity>();
    permissions.add(null);
    permissions.add(new PermissionJpaEntity(
        UUID.randomUUID(),
        "READ",
        "USER",
        "Allows reading users",
        "ACTIVE",
        now,
        now
    ));

    final var jpaEntity = new RoleJpaEntity(
        id,
        "ADMIN",
        "Role with all permissions",
        "ACTIVE",
        now,
        now,
        permissions
    );

    final var result = jpaEntity.toString();
    assertTrue(result.contains("null"));
    assertTrue(result.contains("PermissionJpaEntity"));
  }

  @Test
  void shouldFormatToStringCorrectlyWhenLazyInitializationExceptionOccurs() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();

    final var permissions = mock(List.class);
    when(permissions.stream()).thenThrow(new LazyInitializationException(""));

    final var jpaEntity = new RoleJpaEntity(
        id,
        "ADMIN",
        "Role with all permissions",
        "ACTIVE",
        now,
        now,
        permissions
    );

    final var result = jpaEntity.toString();
    assertTrue(result.contains("permissions=null"));
  }
}
