package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdateUserRequest;

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

  public static UpdateUserDto from(final UpdateUserRequest request) {
    return new UpdateUserDto(
        request.name(),
        request.status(),
        request.roleId()
    );
  }
}
