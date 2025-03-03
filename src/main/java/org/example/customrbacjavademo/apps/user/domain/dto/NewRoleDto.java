package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;

import java.util.List;
import java.util.UUID;

public record NewRoleDto(
    String name,
    String description,
    RoleStatus status,
    List<UUID> permissionIds
) {
  public static NewRoleDto of(
      final String name,
      final String description,
      final RoleStatus status,
      final List<UUID> permissionIds
  ) {
    return new NewRoleDto(name, description, status, permissionIds);
  }
}
