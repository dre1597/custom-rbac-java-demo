package org.example.customrbacjavademo.apps.user.domain.mocks;

import org.example.customrbacjavademo.apps.user.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.apps.user.domain.entities.Role;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;

import java.util.List;

public class RoleTestMocks {
  public static Role createActiveTestRole() {
    var permissions = List.of(PermissionTestMocks.createActiveTestPermission());

    return Role.newRole(NewRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE, permissions));
  }
}
