package org.example.customrbacjavademo.apps.user.domain.mocks;

import org.example.customrbacjavademo.apps.user.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.apps.user.domain.entities.Role;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaEntity;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;

import java.util.List;
import java.util.UUID;

public class RoleTestMocks {
  public static Role createActiveTestRole() {
    final var permissionIds = List.of(PermissionTestMocks.createActiveTestPermission().getId());

    return Role.newRole(NewRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE.name(), permissionIds.stream().map(UUID::toString).toList()));
  }

  public static Role createActiveTestRole(final List<UUID> permissionIds) {
    return Role.newRole(NewRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE.name(), permissionIds.stream().map(UUID::toString).toList()));
  }

  public static Role createActiveTestRole(final String name, final List<UUID> permissionIds) {
    return Role.newRole(NewRoleDto.of(name, "any_description", RoleStatus.ACTIVE.name(), permissionIds.stream().map(UUID::toString).toList()));
  }

  public static Role createActiveTestRole(final String name, final String description, final List<UUID> permissionIds) {
    return Role.newRole(NewRoleDto.of(name, description, RoleStatus.ACTIVE.name(), permissionIds.stream().map(UUID::toString).toList()));
  }

  public static RoleJpaEntity createActiveTestRoleJpa() {
    final var role = RoleMapper.entityToJpa(createActiveTestRole());
    final var permissions = role.getPermissions();

    return new RoleJpaEntity(
        role.getId(),
        role.getName(),
        role.getDescription(),
        role.getStatus(),
        role.getCreatedAt(),
        role.getUpdatedAt(),
        permissions
    );
  }
}
