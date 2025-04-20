package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreateRoleRequest;

import java.util.List;

public record NewRoleDto(
    String name,
    String description,
    String status,
    List<String> permissionIds
) {
  public static NewRoleDto of(
      final String name,
      final String description,
      final String status,
      final List<String> permissionIds
  ) {
    return new NewRoleDto(
        name,
        description,
        status,
        permissionIds
    );
  }

  public static NewRoleDto from(final CreateRoleRequest request) {
    return new NewRoleDto(
        request.name(),
        request.description(),
        request.status(),
        request.permissionIds()
    );
  }
}
