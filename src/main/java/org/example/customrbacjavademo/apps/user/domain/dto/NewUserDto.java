package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.domain.enums.UserStatus;

import java.util.UUID;

public record NewUserDto(
    String name,
    String password,
    UserStatus status,
    UUID roleId
) {
  public static NewUserDto of(final String name, final String password, final UserStatus status, final UUID roleId) {
    return new NewUserDto(name, password, status, roleId);
  }
}
