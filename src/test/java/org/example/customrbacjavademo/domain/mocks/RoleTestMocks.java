package org.example.customrbacjavademo.domain.mocks;

import org.example.customrbacjavademo.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.domain.entities.Role;
import org.example.customrbacjavademo.domain.entities.RoleStatus;

public class RoleTestMocks {
  public static Role createActiveTestRole() {
    return Role.newRole(NewRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE));
  }
}
