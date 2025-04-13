package org.example.customrbacjavademo.apps.user.domain.dto;

public record UpdatePermissionDto(
    String name,
    String scope,
    String description,
    String status
) {
  public static UpdatePermissionDto of(final String name, final String scope, final String description, final String status) {
    return new UpdatePermissionDto(name, scope, description, status);
  }
}
