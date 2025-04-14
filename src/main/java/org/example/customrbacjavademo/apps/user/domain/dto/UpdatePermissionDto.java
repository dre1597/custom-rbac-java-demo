package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.UpdatePermissionRequest;

public record UpdatePermissionDto(
    String name,
    String scope,
    String description,
    String status
) {
  public static UpdatePermissionDto of(final String name, final String scope, final String description, final String status) {
    return new UpdatePermissionDto(name, scope, description, status);
  }

  public static UpdatePermissionDto from(final UpdatePermissionRequest request) {
    return new UpdatePermissionDto(request.name(), request.scope(), request.description(), request.status());
  }
}
