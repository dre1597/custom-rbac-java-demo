package org.example.customrbacjavademo.apps.user.usecase.role.mappers;

import org.example.customrbacjavademo.apps.user.domain.entities.Role;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaEntity;

public final class RoleMapper {
  private RoleMapper() {
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
