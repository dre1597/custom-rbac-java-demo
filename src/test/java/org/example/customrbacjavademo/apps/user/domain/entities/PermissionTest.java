package org.example.customrbacjavademo.apps.user.domain.entities;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdatePermissionDto;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PermissionTest {
  @Test
  void shouldCreatePermission() {
    final var dto = NewPermissionDto.of(PermissionName.READ.name(), PermissionScope.USER.name(), "any_description", PermissionStatus.ACTIVE.name());
    final var newPermission = Permission.newPermission(dto);

    assertNotNull(newPermission.getId());
    assertEquals(dto.name(), newPermission.getName().name());
    assertEquals(dto.scope(), newPermission.getScope().name());
    assertEquals(dto.description(), newPermission.getDescription());
    assertEquals(dto.status(), newPermission.getStatus().name());
    assertNotNull(newPermission.getCreatedAt());
    assertNotNull(newPermission.getUpdatedAt());
  }

  @ParameterizedTest
  @CsvSource({
      "null, USER, any_description, ACTIVE, name is required",
      "READ, null, any_description, ACTIVE, scope is required",
      "READ, USER, null, ACTIVE, description is required",
      "READ, USER, '', ACTIVE, description is required",
      "READ, USER, any_description, null, status is required",
      "null, null, null, null, 'name is required, scope is required, description is required, status is required'",
  })
  void shouldNotCreatePermissionWithInvalidInput(
      final String name,
      final String scope,
      final String description,
      final String status,
      final String expectedMessage
  ) {
    final var actualName = "null".equals(name) ? null : name;
    final var actualScope = "null".equals(scope) ? null : scope;
    final var actualDescription = "null".equals(description) ? null : description;
    final var actualStatus = "null".equals(status) ? null : status;

    final var dto = NewPermissionDto.of(actualName, actualScope, actualDescription, actualStatus);
    final var exception = org.junit.jupiter.api.Assertions.assertThrows(ValidationException.class, () -> Permission.newPermission(dto));
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldUpdateRole() {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var dto = UpdatePermissionDto.of(PermissionName.READ.name(), PermissionScope.USER.name(), "any_description", PermissionStatus.ACTIVE.name());

    permission.update(dto);
    assertEquals(dto.name(), permission.getName().name());
    assertEquals(dto.scope(), permission.getScope().name());
    assertEquals(dto.description(), permission.getDescription());
    assertEquals(dto.status(), permission.getStatus().name());
    assertNotNull(permission.getUpdatedAt());
  }

  @ParameterizedTest
  @CsvSource({
      "null, USER, any_description, ACTIVE, name is required",
      "READ, null, any_description, ACTIVE, scope is required",
      "READ, USER, null, ACTIVE, description is required",
      "READ, USER, '', ACTIVE, description is required",
      "READ, USER, any_description, null, status is required",
      "null, null, null, null, 'name is required, scope is required, description is required, status is required'",
  })
  void shouldNotUpdatePermissionWithInvalidInput(
      final String name,
      final String scope,
      final String description,
      final String status,
      final String expectedMessage
  ) {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var actualName = "null".equals(name) ? null : name;
    final var actualScope = "null".equals(scope) ? null : scope;
    final var actualDescription = "null".equals(description) ? null : description;
    final var actualStatus = "null".equals(status) ? null : status;

    final var dto = UpdatePermissionDto.of(actualName, actualScope, actualDescription, actualStatus);
    final var exception = org.junit.jupiter.api.Assertions.assertThrows(ValidationException.class, () -> permission.update(dto));
    assertEquals(expectedMessage, exception.getMessage());
  }
}
