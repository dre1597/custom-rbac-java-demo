package org.example.customrbacjavademo.apps.user.domain.entities;

import org.example.customrbacjavademo.apps.user.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateRoleDto;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {
  @Test
  void shouldCreateRole() {
    final var permissionIds = List.of(PermissionTestMocks.createActiveTestPermission().getId());
    final var dto = NewRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE.name(), permissionIds.stream().map(UUID::toString).toList());
    final var role = Role.newRole(dto);

    assertNotNull(role.getId());
    assertEquals(dto.name(), role.getName());
    assertEquals(dto.description(), role.getDescription());
    assertEquals(dto.status(), role.getStatus().name());
    assertEquals(dto.permissionIds(), role.getPermissionIds().stream().map(UUID::toString).toList());
    assertNotNull(role.getCreatedAt());
    assertNotNull(role.getUpdatedAt());
  }

  @ParameterizedTest
  @CsvSource({
      "null, any_description, ACTIVE, name is required",
      "'', any_description, ACTIVE, name is required",
      "any_name, null, ACTIVE, description is required",
      "any_name, '', ACTIVE, description is required",
      "any_name, any_description, null, status is required",
      "null, null, null, 'name is required, description is required, status is required'",
  })
  void shouldNotCreateRoleWithInvalidInput(
      final String name,
      final String description,
      final String status,
      final String expectedMessage
  ) {
    final var actualName = "null".equals(name) ? null : name;
    final var actualDescription = "null".equals(description) ? null : description;
    final var actualStatus = "null".equals(String.valueOf(status)) ? null : status;
    final var permissionIds = List.of(PermissionTestMocks.createActiveTestPermission().getId());
    final var dto = NewRoleDto.of(actualName, actualDescription, actualStatus, permissionIds.stream().map(UUID::toString).toList());

    final var exception = assertThrows(
        ValidationException.class,
        () -> Role.newRole(dto)
    );

    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldNotCreateRoleWithoutPermissions() {
    final var nullDto = NewRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE.name(), null);

    var exception = assertThrows(
        ValidationException.class,
        () -> Role.newRole(nullDto)
    );

    assertEquals("at least one permissionId is required", exception.getMessage());

    final var emptyListDto = NewRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE.name(), List.of());
    exception = assertThrows(
        ValidationException.class,
        () -> Role.newRole(emptyListDto)
    );

    assertEquals("at least one permissionId is required", exception.getMessage());
  }

  @Test
  void shouldUpdateRole() {
    final var role = RoleTestMocks.createActiveTestRole();
    final var permissionIds = List.of(PermissionTestMocks.createActiveTestPermission().getId());
    final var dto = UpdateRoleDto.of("updated_name", "updated_description", RoleStatus.INACTIVE.name(), permissionIds.stream().map(UUID::toString).toList());
    final var updatedRole = role.update(dto);

    assertEquals(dto.name(), updatedRole.getName());
    assertEquals(dto.description(), updatedRole.getDescription());
    assertEquals(dto.status(), role.getStatus().name());
  }

  @ParameterizedTest
  @CsvSource({
      "null, any_description, ACTIVE, name is required",
      "'', any_description, ACTIVE, name is required",
      "any_name, null, ACTIVE, description is required",
      "any_name, '', ACTIVE, description is required",
      "any_name, any_description, null, status is required",
      "null, null, null, 'name is required, description is required, status is required'",
  })
  void shouldNotUpdateRoleWithInvalidInput(
      final String name,
      final String description,
      final String status,
      final String expectedMessage
  ) {
    final var role = RoleTestMocks.createActiveTestRole();
    final var actualName = "null".equals(name) ? null : name;
    final var actualDescription = "null".equals(description) ? null : description;
    final var actualStatus = "null".equals(String.valueOf(status)) ? null : status;
    final var dto = UpdateRoleDto.of(actualName, actualDescription, actualStatus, role.getPermissionIds().stream().map(UUID::toString).toList());

    var exception = assertThrows(
        ValidationException.class,
        () -> role.update(dto)
    );

    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldNotUpdateRoleWithoutPermissions() {
    final var role = RoleTestMocks.createActiveTestRole();
    final var nullDto = UpdateRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE.name(), null);
    var exception = assertThrows(
        ValidationException.class,
        () -> role.update(nullDto)
    );

    assertEquals("at least one permissionId is required", exception.getMessage());

    final var emptyListDto = UpdateRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE.name(), List.of());
    exception = assertThrows(
        ValidationException.class,
        () -> role.update(emptyListDto)
    );

    assertEquals("at least one permissionId is required", exception.getMessage());
  }
}
