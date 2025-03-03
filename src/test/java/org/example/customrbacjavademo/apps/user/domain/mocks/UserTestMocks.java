package org.example.customrbacjavademo.apps.user.domain.mocks;

import org.example.customrbacjavademo.apps.user.domain.dto.NewUserDto;
import org.example.customrbacjavademo.apps.user.domain.entities.User;
import org.example.customrbacjavademo.apps.user.domain.enums.UserStatus;

public class UserTestMocks {
  public static User createActiveTestUser() {
    var role = RoleTestMocks.createActiveTestRole();
    return User.newUser(NewUserDto.of("any_name", "any_password", UserStatus.ACTIVE, role.getId()));
  }
}
