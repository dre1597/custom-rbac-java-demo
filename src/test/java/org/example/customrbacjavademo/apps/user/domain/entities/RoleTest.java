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

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {
  @Test
  void shouldCreateRole() {
    var permissionIds = List.of(PermissionTestMocks.createActiveTestPermission().getId());
    var dto = NewRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE, permissionIds);
    var role = Role.newRole(dto);

    assertNotNull(role.getId());
    assertEquals(dto.name(), role.getName());
    assertEquals(dto.description(), role.getDescription());
    assertEquals(dto.status(), role.getStatus());
    assertEquals(dto.permissionIds(), role.getPermissionIds());
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
    var actualName = "null".equals(name) ? null : name;
    var actualDescription = "null".equals(description) ? null : description;
    var actualStatus = "null".equals(String.valueOf(status)) ? null : RoleStatus.valueOf(status);
    var permissionIds = List.of(PermissionTestMocks.createActiveTestPermission().getId());
    var dto = NewRoleDto.of(actualName, actualDescription, actualStatus, permissionIds);

    var exception = assertThrows(
        ValidationException.class,
        () -> Role.newRole(dto)
    );

    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldNotCreateRoleWithoutPermissions() {
    var dto = NewRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE, null);

    var exception = assertThrows(
        ValidationException.class,
        () -> Role.newRole(dto)
    );

    assertEquals("at least one permissionId is required", exception.getMessage());
  }

  @Test
  void shouldUpdateRole() {
    var role = RoleTestMocks.createActiveTestRole();
    var permissionIds = List.of(PermissionTestMocks.createActiveTestPermission().getId());
    var dto = UpdateRoleDto.of("updated_name", "updated_description", RoleStatus.INACTIVE, permissionIds);
    var updatedRole = role.update(dto);

    assertEquals(dto.name(), updatedRole.getName());
    assertEquals(dto.description(), updatedRole.getDescription());
    assertEquals(dto.status(), role.getStatus());
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
    var role = RoleTestMocks.createActiveTestRole();
    var actualName = "null".equals(name) ? null : name;
    var actualDescription = "null".equals(description) ? null : description;
    var actualStatus = "null".equals(String.valueOf(status)) ? null : RoleStatus.valueOf(status);
    var dto = UpdateRoleDto.of(actualName, actualDescription, actualStatus, role.getPermissionIds());

    var exception = assertThrows(
        ValidationException.class,
        () -> role.update(dto)
    );

    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldNotUpdateRoleWithoutPermissions() {
    var role = RoleTestMocks.createActiveTestRole();
    var dto = UpdateRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE, null);
    var exception = assertThrows(
        ValidationException.class,
        () -> role.update(dto)
    );

    assertEquals("at least one permissionId is required", exception.getMessage());
  }
}
