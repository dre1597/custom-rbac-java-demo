package org.example.customrbacjavademo.apps.user.domain.dto;

import java.util.List;

public record NewRoleDto(
    String name,
    String description,
    String status,
    List<String> permissionIds
) {
  public static NewRoleDto of(
      final String name,
      final String description,
      final String status,
      final List<String> permissionIds
  ) {
    return new NewRoleDto(
        name,
        description,
        status,
        permissionIds
    );
  }
}
