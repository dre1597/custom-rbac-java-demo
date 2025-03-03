package org.example.customrbacjavademo.apps.user.usecase.mappers;

import org.example.customrbacjavademo.apps.user.domain.entities.Permission;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaEntity;

public class PermissionMapper {
  private PermissionMapper() {
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
