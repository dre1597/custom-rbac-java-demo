package org.example.customrbacjavademo.domain.dto;

import org.example.customrbacjavademo.domain.entities.RoleStatus;

public record NewRoleDto(
    String name,
    String description,
    RoleStatus status
) {
  public static NewRoleDto of(final String name, final String description, final RoleStatus status) {
    return new NewRoleDto(name, description, status);
  }
}
