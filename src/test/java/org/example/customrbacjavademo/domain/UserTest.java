package org.example.customrbacjavademo.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
  @Test
  void shouldCreateUser() {
    var user = User.newUser("any_name", "any_password", UserStatus.ACTIVE);

    assertNotNull(user.getId());
    assertEquals("any_name", user.getName());
    assertEquals("any_password", user.getPassword());
    assertEquals(UserStatus.ACTIVE, user.getStatus());
    assertNotNull(user.getCreatedAt());
    assertNotNull(user.getUpdatedAt());
  }

  @ParameterizedTest
  @CsvSource({
      "null, any_password, name is required",
      "any_name, null, password is required",
  })
  void shouldNotCreateUserWithInvalidInput(final String name, final String password, final String expectedMessage) {
    var exception = assertThrows(
        IllegalArgumentException.class,
        () -> User.newUser(
            "null".equals(name) ? null : name,
            "null".equals(password) ? null : password,
            UserStatus.ACTIVE
        )
    );
    assertEquals(expectedMessage, exception.getMessage());
  }
}
