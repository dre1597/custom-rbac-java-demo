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
    var newPermissionDto = NewPermissionDto.of(PermissionName.READ, PermissionScope.USER, "any_description", PermissionStatus.ACTIVE);
    var permission = Permission.newPermission(newPermissionDto);

    assertNotNull(permission.getId());
    assertEquals(newPermissionDto.name(), permission.getName());
    assertEquals(newPermissionDto.scope(), permission.getScope());
    assertEquals(newPermissionDto.description(), permission.getDescription());
    assertEquals(newPermissionDto.status(), permission.getStatus());
    assertNotNull(permission.getCreatedAt());
    assertNotNull(permission.getUpdatedAt());
  }

  @ParameterizedTest
  @CsvSource({
      "null, USER, any_description, ACTIVE, name is required",
      "READ, null, any_description, ACTIVE, scope is required",
      "READ, USER, null, ACTIVE, description is required",
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
    var actualName = "null".equals(name) ? null : PermissionName.valueOf(name);
    var actualScope = "null".equals(scope) ? null : PermissionScope.valueOf(scope);
    var actualDescription = "null".equals(description) ? null : description;
    var actualStatus = "null".equals(status) ? null : PermissionStatus.valueOf(status);

    var newPermissionDto = NewPermissionDto.of(actualName, actualScope, actualDescription, actualStatus);
    var exception = org.junit.jupiter.api.Assertions.assertThrows(ValidationException.class, () -> Permission.newPermission(newPermissionDto));
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldUpdateRole() {
    var permission = PermissionTestMocks.createActiveTestPermission();
    var updatePermissionDto = UpdatePermissionDto.of(PermissionName.READ, PermissionScope.USER, "any_description", PermissionStatus.ACTIVE);

    permission.update(updatePermissionDto);
    assertEquals(updatePermissionDto.name(), permission.getName());
    assertEquals(updatePermissionDto.scope(), permission.getScope());
    assertEquals(updatePermissionDto.description(), permission.getDescription());
    assertEquals(updatePermissionDto.status(), permission.getStatus());
    assertNotNull(permission.getUpdatedAt());
  }

  @ParameterizedTest
  @CsvSource({
      "null, USER, any_description, ACTIVE, name is required",
      "READ, null, any_description, ACTIVE, scope is required",
      "READ, USER, null, ACTIVE, description is required",
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
    var permission = PermissionTestMocks.createActiveTestPermission();
    var actualName = "null".equals(name) ? null : PermissionName.valueOf(name);
    var actualScope = "null".equals(scope) ? null : PermissionScope.valueOf(scope);
    var actualDescription = "null".equals(description) ? null : description;
    var actualStatus = "null".equals(status) ? null : PermissionStatus.valueOf(status);

    var updatePermissionDto = UpdatePermissionDto.of(actualName, actualScope, actualDescription, actualStatus);
    var exception = org.junit.jupiter.api.Assertions.assertThrows(ValidationException.class, () -> permission.update(updatePermissionDto));
    assertEquals(expectedMessage, exception.getMessage());
  }
}
