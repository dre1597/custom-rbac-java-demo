package org.example.customrbacjavademo.apps.user.domain.dto;

public record UpdateUserDto(String name, String status, String roleId) {
  public static UpdateUserDto of(
      final String name,
      final String status,
      final String roleId
  ) {
    return new UpdateUserDto(
        name,
        status,
        roleId
    );
  }
}
