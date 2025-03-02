package org.example.customrbacjavademo.domain.mocks;

import org.example.customrbacjavademo.domain.dto.NewUserDto;
import org.example.customrbacjavademo.domain.entities.User;
import org.example.customrbacjavademo.domain.entities.UserStatus;

public class UserTestMocks {
  public static User createTestUser() {
    return User.newUser(NewUserDto.of("any_name", "any_password", UserStatus.ACTIVE));
  }
}
