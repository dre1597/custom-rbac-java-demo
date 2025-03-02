package org.example.customrbacjavademo.domain.dto;

import org.example.customrbacjavademo.domain.entities.UserStatus;

public record UpdateUserDto(String name, UserStatus status) {
  public static UpdateUserDto of(
      final String name,
      final UserStatus status
  ) {
    return new UpdateUserDto(name, status);
  }
}
