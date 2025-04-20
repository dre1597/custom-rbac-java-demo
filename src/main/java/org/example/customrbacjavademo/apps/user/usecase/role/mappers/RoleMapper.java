package org.example.customrbacjavademo.apps.user.usecase.role.mappers;

import org.example.customrbacjavademo.apps.user.domain.entities.Role;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.RoleDetailsResponse;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.RoleResponse;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaEntity;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;

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

  public static RoleResponse entityToResponse(final Role entity) {
    return new RoleResponse(
        entity.getId().toString(),
        entity.getName(),
        entity.getDescription(),
        entity.getStatus().toString(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }

  public static RoleDetailsResponse jpaToDetailsResponse(final RoleJpaEntity jpa) {
    final var permissions = jpa.getPermissions().stream()
        .map(PermissionMapper::jpaToResponse)
        .toList();

    return new RoleDetailsResponse(
        jpa.getId().toString(),
        jpa.getName(),
        jpa.getDescription(),
        jpa.getStatus(),
        jpa.getCreatedAt(),
        jpa.getUpdatedAt(),
        permissions
    );
  }

  public static RoleResponse jpaToResponse(final RoleJpaEntity jpa) {
    return new RoleResponse(
        jpa.getId().toString(),
        jpa.getName(),
        jpa.getDescription(),
        jpa.getStatus(),
        jpa.getCreatedAt(),
        jpa.getUpdatedAt()
    );
  }
}
