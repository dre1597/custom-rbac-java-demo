package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.domain.entities.Permission;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;

import java.util.List;

public record NewRoleDto(
    String name,
    String description,
    RoleStatus status,
    List<Permission> permissions
) {
  public static NewRoleDto of(
      final String name,
      final String description,
      final RoleStatus status,
      final List<Permission> permissions
  ) {
    return new NewRoleDto(name, description, status, permissions);
  }
}
