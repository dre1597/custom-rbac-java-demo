package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.infra.api.dto.requests.CreatePermissionRequest;

public record NewPermissionDto
    (
        String name,
        String scope,
        String description,
        String status
    ) {
  public static NewPermissionDto of(final String name, final String scope, final String description, final String status) {
    return new NewPermissionDto(name, scope, description, status);
  }

  public static NewPermissionDto from(final CreatePermissionRequest request) {
    return new NewPermissionDto(request.name(), request.scope(), request.description(), request.status());
  }
}
