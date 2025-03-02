package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;

public record UpdateRoleDto(
    String name,
    String description,
    RoleStatus status
) {
  public static UpdateRoleDto of(final String name, final String description, final RoleStatus status) {
    return new UpdateRoleDto(name, description, status);
  }
}
