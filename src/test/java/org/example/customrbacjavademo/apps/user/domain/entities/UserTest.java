package org.example.customrbacjavademo.apps.user.domain.entities;

import org.example.customrbacjavademo.apps.user.domain.dto.NewUserDto;
import org.example.customrbacjavademo.apps.user.domain.dto.UpdateUserDto;
import org.example.customrbacjavademo.apps.user.domain.enums.UserStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.domain.services.PasswordService;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
  @Test
  void shouldCreateUser() {
    var password = "any_password";
    var dto = NewUserDto.of("any_name", password, UserStatus.ACTIVE);
    var user = User.newUser(dto);

    assertNotNull(user.getId());
    assertEquals(dto.name(), user.getName());
    assertTrue(PasswordService.matches(password, user.getPassword()));
    assertEquals(dto.status(), user.getStatus());
    assertNotNull(user.getCreatedAt());
    assertNotNull(user.getUpdatedAt());
  }

  @ParameterizedTest
  @CsvSource({
      "null, any_password, ACTIVE, name is required",
      "'', any_password, ACTIVE, name is required",
      "any_name, null, ACTIVE, password is required",
      "any_name, '', ACTIVE, password is required",
      "any_name, any_password, null, status is required",
      "null, null, null, 'name is required, password is required, status is required'",
  })
  void shouldNotCreateUserWithInvalidInput(
      final String name,
      final String password,
      final String status,
      final String expectedMessage
  ) {
    var actualName = "null".equals(name) ? null : name;
    var actualPassword = "null".equals(password) ? null : password;
    var actualStatus = "null".equals(String.valueOf(status)) ? null : UserStatus.valueOf(status);

    var dto = new NewUserDto(actualName, actualPassword, actualStatus);

    var exception = assertThrows(
        ValidationException.class,
        () -> User.newUser(dto)
    );

    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldUpdateUser() {
    var user = UserTestMocks.createActiveTestUser();
    var dto = UpdateUserDto.of("updated_name", UserStatus.INACTIVE);
    var updatedUser = user.update(dto);

    assertEquals(dto.name(), updatedUser.getName());
    assertEquals(dto.status(), updatedUser.getStatus());
  }

  @ParameterizedTest
  @CsvSource({
      "null, ACTIVE, name is required",
      "'', ACTIVE, name is required",
      "any_name, null, status is required",
      "null, null, 'name is required, status is required'",
  })
  void shouldNotUpdateUserWithInvalidInput(final String name, final String status, final String expectedMessage) {
    var user = UserTestMocks.createActiveTestUser();

    var actualName = "null".equals(name) ? null : name;
    var actualStatus = "null".equals(String.valueOf(status)) ? null : UserStatus.valueOf(status);

    var dto = UpdateUserDto.of(actualName, actualStatus);

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
