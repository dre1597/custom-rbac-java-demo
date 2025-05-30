package org.example.customrbacjavademo.apps.user.domain.mocks;

import org.example.customrbacjavademo.apps.user.domain.dto.NewUserDto;
import org.example.customrbacjavademo.apps.user.domain.entities.User;
import org.example.customrbacjavademo.apps.user.domain.enums.UserStatus;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;

import java.util.UUID;

public class UserTestMocks {
  public static User createActiveTestUser() {
    final var role = RoleTestMocks.createActiveTestRole();
    return User.newUser(NewUserDto.of("any_name", "any_password", UserStatus.ACTIVE.name(), role.getId().toString()));
  }

  public static User createActiveTestUser(final UUID roleId) {
    return User.newUser(NewUserDto.of("any_name", "any_password", UserStatus.ACTIVE.name(), roleId.toString()));
  }

  public static User createActiveTestUser(final String name, final UUID roleId) {
    return User.newUser(NewUserDto.of(name, "any_password", UserStatus.ACTIVE.name(), roleId.toString()));
  }

  public static User createActiveTestUser(final String name, final String password) {
    final var role = RoleTestMocks.createActiveTestRole();
    return User.newUser(NewUserDto.of(name, password, UserStatus.ACTIVE.name(), role.getId().toString()));
  }

  public static User createActiveTestUser(final String name, final String password, final UUID roleId) {
    return User.newUser(NewUserDto.of(name, password, UserStatus.ACTIVE.name(), roleId.toString()));
  }

  public static UserJpaEntity createActiveTestUserJpa() {
    final var user = UserMapper.entityToJpa(createActiveTestUser());
    final var role = user.getRole();

    return new UserJpaEntity(
        user.getId(),
        user.getName(),
        user.getPassword(),
        user.getStatus(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        role
    );
  }
}
