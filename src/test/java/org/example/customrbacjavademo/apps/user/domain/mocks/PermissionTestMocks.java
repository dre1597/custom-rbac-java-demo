package org.example.customrbacjavademo.apps.user.domain.mocks;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.entities.Permission;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;

public class PermissionTestMocks {
  public static Permission createActiveTestPermission() {
    return Permission.newPermission(NewPermissionDto.of(PermissionName.READ, PermissionScope.USER, "any_description", PermissionStatus.ACTIVE));
  }
}
