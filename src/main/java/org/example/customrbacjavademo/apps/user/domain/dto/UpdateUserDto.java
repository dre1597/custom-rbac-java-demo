package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.domain.enums.UserStatus;

import java.util.UUID;

public record UpdateUserDto(String name, UserStatus status, UUID roleId) {
  public static UpdateUserDto of(
      final String name,
      final UserStatus status,
      final UUID roleId
  ) {
    return new UpdateUserDto(name, status, roleId);
  }
}
