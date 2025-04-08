package org.example.customrbacjavademo.apps.user.usecase.user.mappers;

import org.example.customrbacjavademo.apps.user.domain.entities.User;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserDetailsResponse;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;

public final class UserMapper {
  private UserMapper() {
  }

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

  public static UserDetailsResponse jpaToDetailsResponse(final UserJpaEntity jpa) {
    final var role = RoleMapper.jpaToResponse(jpa.getRole());

    return new UserDetailsResponse(
        jpa.getId(),
        jpa.getName(),
        jpa.getStatus(),
        jpa.getCreatedAt(),
        jpa.getUpdatedAt(),
        role
    );
  }
}
