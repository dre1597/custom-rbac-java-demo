package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;

public record NewRoleDto(
    String name,
    String description,
    RoleStatus status
) {
  public static NewRoleDto of(final String name, final String description, final RoleStatus status) {
    return new NewRoleDto(name, description, status);
  }
}
