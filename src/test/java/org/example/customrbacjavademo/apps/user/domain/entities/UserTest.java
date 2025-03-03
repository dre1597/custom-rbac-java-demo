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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
  @Test
  void shouldCreateUser() {
    var password = "any_password";
    var role = RoleTestMocks.createActiveTestRole();
    var dto = NewUserDto.of("any_name", password, UserStatus.ACTIVE, role.getId());
    var user = User.newUser(dto);

    assertNotNull(user.getId());
    assertEquals(dto.name(), user.getName());
    assertTrue(PasswordService.matches(password, user.getPassword()));
    assertEquals(dto.status(), user.getStatus());
    assertEquals(dto.roleId(), user.getRoleId());
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
    var actualName = "null".equals(name) ? null : name;
    var actualPassword = "null".equals(password) ? null : password;
    var actualStatus = "null".equals(String.valueOf(status)) ? null : UserStatus.valueOf(status);
    var actualRoleId = "null".equals(roleId) ? null : UUID.fromString(roleId);

    var dto = new NewUserDto(actualName, actualPassword, actualStatus, actualRoleId);

    var exception = assertThrows(
        ValidationException.class,
        () -> User.newUser(dto)
    );

    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldUpdateUser() {
    var user = UserTestMocks.createActiveTestUser();
    var newRole = RoleTestMocks.createActiveTestRole();
    var dto = UpdateUserDto.of("updated_name", UserStatus.INACTIVE, newRole.getId());
    var updatedUser = user.update(dto);

    assertEquals(dto.name(), updatedUser.getName());
    assertEquals(dto.status(), updatedUser.getStatus());
    assertEquals(dto.roleId(), updatedUser.getRoleId());
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
    var user = UserTestMocks.createActiveTestUser();

    var actualName = "null".equals(name) ? null : name;
    var actualStatus = "null".equals(String.valueOf(status)) ? null : UserStatus.valueOf(status);
    var actualRoleId = "null".equals(String.valueOf(roleId)) ? null : UUID.fromString(roleId);

    var dto = UpdateUserDto.of(actualName, actualStatus, actualRoleId);

    var exception = assertThrows(
        ValidationException.class,
        () -> user.update(dto)
    );
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldUpdatePasswordAndKeepItEncrypted() {
    var user = UserTestMocks.createActiveTestUser();

    var updatedUser = user.updatePassword("updated_password");

    assertTrue(PasswordService.matches("updated_password", updatedUser.getPassword()));
  }

  @Test
  void shouldNotUpdatePasswordWithInvalidInput() {
    var user = UserTestMocks.createActiveTestUser();

    var exception = assertThrows(
        ValidationException.class,
        () -> user.updatePassword(null)
    );
    assertEquals("password is required", exception.getMessage());

    exception = assertThrows(
        ValidationException.class,
        () -> user.updatePassword("")
    );
    assertEquals("password is required", exception.getMessage());

    exception = assertThrows(
        ValidationException.class,
        () -> user.updatePassword(" ")
    );
    assertEquals("password is required", exception.getMessage());
  }
}
