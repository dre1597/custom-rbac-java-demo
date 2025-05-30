package org.example.customrbacjavademo.apps.user.domain.entities;

import org.example.customrbacjavademo.apps.user.domain.dto.NewUserDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateUserDto;
import org.example.customrbacjavademo.apps.user.domain.enums.UserStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.domain.services.PasswordService;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
  @Test
  void shouldCreateUser() {
    final var password = "any_password";
    final var role = RoleTestMocks.createActiveTestRole();
    final var dto = NewUserDto.of("any_name", password, UserStatus.ACTIVE.name(), role.getId().toString());
    final var user = User.newUser(dto);

    assertNotNull(user.getId());
    assertEquals(dto.name(), user.getName());
    assertTrue(PasswordService.matches(password, user.getPassword()));
    assertEquals(dto.status(), user.getStatus().toString());
    assertEquals(dto.roleId(), user.getRoleId().toString());
    assertNotNull(user.getCreatedAt());
    assertNotNull(user.getUpdatedAt());
  }

  @ParameterizedTest
  @CsvSource({
      "null, any_password, ACTIVE, 076b072b-9c40-409c-a899-771498acbc87, name is required",
      "'', any_password, ACTIVE, 076b072b-9c40-409c-a899-771498acbc87, name is required",
      "any_name, null, ACTIVE, 076b072b-9c40-409c-a899-771498acbc87, password is required",
      "any_name, '', ACTIVE, 076b072b-9c40-409c-a899-771498acbc87, password is required",
      "any_name, any_password, null, 076b072b-9c40-409c-a899-771498acbc87, status is required",
      "any_name, any_password, ACTIVE, null, roleId is required",
      "null, null, null, null, 'name is required, password is required, status is required, roleId is required'",
  })
  void shouldNotCreateUserWithInvalidInput(
      final String name,
      final String password,
      final String status,
      final String roleId,
      final String expectedMessage
  ) {
    final var actualName = "null".equals(name) ? null : name;
    final var actualPassword = "null".equals(password) ? null : password;
    final var actualStatus = "null".equals(String.valueOf(status)) ? null : status;
    final var actualRoleId = "null".equals(roleId) ? null : roleId;

    final var dto = new NewUserDto(actualName, actualPassword, actualStatus, actualRoleId);

    final var exception = assertThrows(
        ValidationException.class,
        () -> User.newUser(dto)
    );

    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldUpdateUser() {
    final var user = UserTestMocks.createActiveTestUser();
    final var newRole = RoleTestMocks.createActiveTestRole();
    final var dto = UpdateUserDto.of("updated_name", UserStatus.INACTIVE.name(), newRole.getId().toString());
    final var updatedUser = user.update(dto);

    assertEquals(dto.name(), updatedUser.getName());
    assertEquals(dto.status(), updatedUser.getStatus().toString());
    assertEquals(dto.roleId(), updatedUser.getRoleId().toString());
  }

  @ParameterizedTest
  @CsvSource({
      "null, ACTIVE, 076b072b-9c40-409c-a899-771498acbc87, name is required",
      "'', ACTIVE, 076b072b-9c40-409c-a899-771498acbc87, name is required",
      "any_name, null, 076b072b-9c40-409c-a899-771498acbc87, status is required",
      "any_name, ACTIVE, null, roleId is required",
      "null, null, null, 'name is required, status is required, roleId is required'",
  })
  void shouldNotUpdateUserWithInvalidInput(final String name, final String status, final String roleId, final String expectedMessage) {
    final var user = UserTestMocks.createActiveTestUser();

    final var actualName = "null".equals(name) ? null : name;
    final var actualStatus = "null".equals(String.valueOf(status)) ? null : status;
    final var actualRoleId = "null".equals(String.valueOf(roleId)) ? null : roleId;

    final var dto = UpdateUserDto.of(actualName, actualStatus, actualRoleId);

    final var exception = assertThrows(
        ValidationException.class,
        () -> user.update(dto)
    );
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldUpdatePasswordAndKeepItEncrypted() {
    final var user = UserTestMocks.createActiveTestUser();

    final var updatedUser = user.updatePassword("updated_password");

    assertTrue(PasswordService.matches("updated_password", updatedUser.getPassword()));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldNotUpdatePasswordWithInvalidInput(final String password) {
    final var user = UserTestMocks.createActiveTestUser();

    final var exception = assertThrows(
        ValidationException.class,
        () -> user.updatePassword(password)
    );
    assertEquals("new password is required", exception.getMessage());
  }
}
