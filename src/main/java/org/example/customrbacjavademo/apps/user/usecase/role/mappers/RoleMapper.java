package org.example.customrbacjavademo.apps.user.usecase.role.mappers;

import org.example.customrbacjavademo.apps.user.domain.entities.Role;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaEntity;

public final class RoleMapper {
  private RoleMapper() {
  }

  public static Role jpaToEntity(final RoleJpaEntity jpa) {
    final var permissionIds = jpa.getPermissions().stream()
        .map(PermissionJpaEntity::getId)
        .toList();

    return Role.with(
        jpa.getId(),
        jpa.getName(),
        jpa.getDescription(),
        RoleStatus.valueOf(jpa.getStatus()),
        jpa.getCreatedAt(),
        jpa.getUpdatedAt(),
        permissionIds
    );
  }

  public static RoleJpaEntity entityToJpa(final Role entity) {
    final var permissions = entity.getPermissionIds().stream()
        .map(id -> {
          var permission = new PermissionJpaEntity();
          permission.setId(id);
          return permission;
        })
        .toList();

    return new RoleJpaEntity(
        entity.getId(),
        entity.getName(),
        entity.getDescription(),
        entity.getStatus().toString(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        permissions
    );
  }
}
