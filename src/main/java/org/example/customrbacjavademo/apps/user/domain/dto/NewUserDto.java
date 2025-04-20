package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreateUserRequest;

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

  public static NewUserDto from(final CreateUserRequest request) {
    return new NewUserDto(
        request.name(),
        request.password(),
        request.status(),
        request.roleId()
    );
  }
}
