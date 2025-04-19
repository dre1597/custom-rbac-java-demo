package org.example.customrbacjavademo.apps.user.domain.dto;

import java.util.List;

public record UpdateRoleDto(
    String name,
    String description,
    String status,
    List<String> permissionIds
) {
  public static UpdateRoleDto of(
      final String name,
      final String description,
      final String status,
      final List<String> permissionIds
  ) {
    return new UpdateRoleDto(name, description, status, permissionIds);
  }
}
