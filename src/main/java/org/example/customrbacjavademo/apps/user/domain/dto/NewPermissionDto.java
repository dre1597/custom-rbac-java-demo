package org.example.customrbacjavademo.apps.user.domain.dto;

import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;

public record NewPermissionDto
    (
        PermissionName name,
        PermissionScope scope,
        String description,
        PermissionStatus status
    ) {
  public static NewPermissionDto of(final PermissionName name, final PermissionScope scope, final String description, final PermissionStatus status) {
    return new NewPermissionDto(name, scope, description, status);
  }
}
