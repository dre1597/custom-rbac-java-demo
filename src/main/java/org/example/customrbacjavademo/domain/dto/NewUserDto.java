package org.example.customrbacjavademo.domain.dto;

import org.example.customrbacjavademo.domain.entities.UserStatus;

public record NewUserDto(
    String name,
    String password,
    UserStatus status
) {
  public static NewUserDto of(final String name, final String password, final UserStatus status) {
    return new NewUserDto(name, password, status);
  }
}
