package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;

import java.util.List;
import java.util.UUID;

public record UpdateRoleDto(
    String name,
    String description,
    RoleStatus status,
    List<UUID> permissionIds
) {
  public static UpdateRoleDto of(
      final String name,
      final String description,
      final RoleStatus status,
      final List<UUID> permissionIds
  ) {
    return new UpdateRoleDto(name, description, status, permissionIds);
  }
}
