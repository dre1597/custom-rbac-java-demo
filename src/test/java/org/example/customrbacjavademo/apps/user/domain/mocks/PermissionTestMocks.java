package org.example.customrbacjavademo.apps.user.domain.mocks;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.entities.Permission;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;

public class PermissionTestMocks {
  public static Permission createActiveTestPermission() {
    return Permission.newPermission(NewPermissionDto.of(PermissionName.READ.name(), PermissionScope.USER.name(), "any_description", PermissionStatus.ACTIVE.name()));
  }

  public static Permission createActiveTestPermission(final String name) {
    return Permission.newPermission(NewPermissionDto.of(name, PermissionScope.USER.name(), "any_description", PermissionStatus.ACTIVE.name()));
  }

  public static Permission createActiveTestPermission(final String name, final String description) {
    return Permission.newPermission(NewPermissionDto.of(name, PermissionScope.USER.name(), description, PermissionStatus.ACTIVE.name()));
  }

  public static Permission createActiveTestPermission(final String name, final String scope, final String description) {
    return Permission.newPermission(NewPermissionDto.of(name, scope, description, PermissionStatus.ACTIVE.name()));
  }
}
