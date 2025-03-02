package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.domain.enums.UserStatus;

public record UpdateUserDto(String name, UserStatus status) {
  public static UpdateUserDto of(
      final String name,
      final UserStatus status
  ) {
    return new UpdateUserDto(name, status);
  }
}
