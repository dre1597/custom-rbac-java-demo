package org.example.customrbacjavademo.apps.user.domain.mocks;

import org.example.customrbacjavademo.apps.user.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.apps.user.domain.entities.Role;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;

public class RoleTestMocks {
  public static Role createActiveTestRole() {
    return Role.newRole(NewRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE));
  }
}
