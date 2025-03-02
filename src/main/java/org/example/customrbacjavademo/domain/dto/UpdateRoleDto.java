package org.example.customrbacjavademo.domain.dto;

import org.example.customrbacjavademo.domain.entities.RoleStatus;

public record UpdateRoleDto(
    String name,
    String description,
    RoleStatus status
) {
  public static UpdateRoleDto of(final String name, final String description, final RoleStatus status) {
    return new UpdateRoleDto(name, description, status);
  }
}
