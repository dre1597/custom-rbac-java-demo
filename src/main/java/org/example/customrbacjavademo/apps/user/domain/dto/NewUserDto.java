package org.example.customrbacjavademo.apps.user.domain.dto;

public record NewUserDto(
    String name,
    String password,
    String status,
    String roleId
) {
  public static NewUserDto of(
      final String name,
      final String password,
      final String status,
      final String roleId
  ) {
    return new NewUserDto(
        name,
        password,
        status,
        roleId
    );
  }
}
