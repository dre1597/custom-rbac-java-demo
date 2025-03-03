package org.example.customrbacjavademo.apps.user.usecase.mappers;

import org.example.customrbacjavademo.apps.user.domain.entities.Permission;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaEntity;

public final class PermissionMapper {
  private PermissionMapper() {
  }

  public static Permission jpaToEntity(final PermissionJpaEntity jpa) {
    return Permission.with(
        jpa.getId(),
        PermissionName.valueOf(jpa.getName()),
        PermissionScope.valueOf(jpa.getScope()),
        jpa.getDescription(),
        PermissionStatus.valueOf(jpa.getStatus()),
        jpa.getCreatedAt(),
        jpa.getUpdatedAt()
    );
  }

  public static PermissionJpaEntity entityToJpa(final Permission entity) {
    return new PermissionJpaEntity(
        entity.getId(),
        entity.getName().name(),
        entity.getScope().name(),
        entity.getDescription(),
        entity.getStatus().name(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }
}
