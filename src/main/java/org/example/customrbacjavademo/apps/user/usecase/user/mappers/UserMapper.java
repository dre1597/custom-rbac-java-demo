package org.example.customrbacjavademo.apps.user.usecase.user.mappers;

import org.example.customrbacjavademo.apps.user.domain.entities.User;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;

public final class UserMapper {
  public static UserJpaEntity entityToJpa(final User entity) {
    final var role = entity.getRoleId();
    final var roleJpa = new RoleJpaEntity();
    roleJpa.setId(role);

    return new UserJpaEntity(
        entity.getId(),
        entity.getName(),
        entity.getPassword(),
        entity.getStatus().toString(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        roleJpa
    );
  }
}
