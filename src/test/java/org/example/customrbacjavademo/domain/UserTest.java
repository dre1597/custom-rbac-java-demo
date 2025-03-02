package org.example.customrbacjavademo.domain;

import org.example.customrbacjavademo.domain.dto.NewUserDto;
import org.example.customrbacjavademo.domain.dto.UpdateUserDto;
import org.example.customrbacjavademo.domain.entities.User;
import org.example.customrbacjavademo.domain.entities.UserStatus;
import org.example.customrbacjavademo.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.domain.services.PasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
  @Test
  void shouldCreateUser() {
    var password = "any_password";
    var user = User.newUser(NewUserDto.of("any_name", password, UserStatus.ACTIVE));

    assertNotNull(user.getId());
    assertEquals("any_name", user.getName());
    assertTrue(PasswordService.matches(password, user.getPassword()));
    assertEquals(UserStatus.ACTIVE, user.getStatus());
    assertNotNull(user.getCreatedAt());
    assertNotNull(user.getUpdatedAt());
  }

  @ParameterizedTest
  @CsvSource({
      "null, any_password, name is required",
  })
  void shouldNotCreateUserWithInvalidInput(final String name, final String password, final String expectedMessage) {
    var exception = assertThrows(
        IllegalArgumentException.class,
        () -> User.newUser(
            NewUserDto.of("null".equals(name) ? null : name, "null".equals(password) ? null : password, UserStatus.ACTIVE)
        )
    );
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldUpdateUser() {
    var user =  UserTestMocks.createActiveTestUser();

    var updatedUser = user.update(UpdateUserDto.of("updated_name"));

    assertEquals("updated_name", updatedUser.getName());
    assertEquals(user.getStatus(), updatedUser.getStatus());
  }

  @ParameterizedTest
  @CsvSource({
      "null, name is required",
  })
  void shouldNotUpdateUserWithInvalidInput(final String name, final String expectedMessage) {
    var user =  UserTestMocks.createActiveTestUser();

    var exception = assertThrows(
        IllegalArgumentException.class,
        () -> user.update(
            UpdateUserDto.of("null".equals(name) ? null : name)
        )
    );
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void shouldActivateUser() {
    var user =  UserTestMocks.createInactiveTestUser();

    var activatedUser = user.activate();

    assertEquals(UserStatus.ACTIVE, activatedUser.getStatus());
  }

  @Test
  void shouldDeactivateUser() {
    var user =  UserTestMocks.createActiveTestUser();

    var deactivatedUser = user.deactivate();

    assertEquals(UserStatus.INACTIVE, deactivatedUser.getStatus());
  }

  @Test
  void shouldUpdatePasswordAndKeepItEncrypted() {
    var user =  UserTestMocks.createActiveTestUser();

    var updatedUser = user.updatePassword("updated_password");

    assertTrue(PasswordService.matches("updated_password", updatedUser.getPassword()));
  }

  @Test
  void shouldNotUpdatePasswordWithInvalidInput() {
    var user =  UserTestMocks.createActiveTestUser();

    var exception = assertThrows(
        IllegalArgumentException.class,
        () -> user.updatePassword(null)
    );
    assertEquals("password is required", exception.getMessage());

    exception = assertThrows(
        IllegalArgumentException.class,
        () -> user.updatePassword("")
    );
    assertEquals("password is required", exception.getMessage());

    exception = assertThrows(
        IllegalArgumentException.class,
        () -> user.updatePassword(" ")
    );
    assertEquals("password is required", exception.getMessage());
  }
}
