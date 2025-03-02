package org.example.customrbacjavademo.apps.user.domain.entities;

import org.example.customrbacjavademo.apps.user.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateRoleDto;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {
  @Test
  void shouldCreateRole() {
    var role = Role.newRole(NewRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE));

    assertNotNull(role.getId());
    assertEquals("any_name", role.getName());
    assertEquals("any_description", role.getDescription());
    assertEquals(RoleStatus.ACTIVE, role.getStatus());
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

    var newRoleDto = NewRoleDto.of(actualName, actualDescription, actualStatus);

    var exception = assertThrows(
        ValidationException.class,
        () -> Role.newRole(newRoleDto)
    );

    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldUpdateRole() {
    var role = RoleTestMocks.createActiveTestRole();

    var updatedRole = role.update(UpdateRoleDto.of("updated_name", "updated_description", RoleStatus.INACTIVE));

    assertEquals("updated_name", updatedRole.getName());
    assertEquals("updated_description", updatedRole.getDescription());
    assertEquals(RoleStatus.INACTIVE, role.getStatus());
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

    var updateRoleDto = UpdateRoleDto.of(actualName, actualDescription, actualStatus);

    var exception = assertThrows(
        ValidationException.class,
        () -> role.update(updateRoleDto)
    );

    assertEquals(expectedMessage, exception.getMessage());
  }
}
