package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;

public record UpdatePermissionDto(
    PermissionName name,
    PermissionScope scope,
    String description,
    PermissionStatus status
) {
  public static UpdatePermissionDto of(final PermissionName name, final PermissionScope scope, final String description, final PermissionStatus status) {
    return new UpdatePermissionDto(name, scope, description, status);
  }
}
