package org.example.customrbacjavademo.apps.user.usecase.user.mappers;

import org.example.customrbacjavademo.apps.user.domain.entities.User;
import org.example.customrbacjavademo.apps.user.domain.enums.UserStatus;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserDetailsResponse;
import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserResponse;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;

public final class UserMapper {
  private UserMapper() {
  }

  public static User jpaToEntity(final UserJpaEntity jpa) {
    final var roleId = jpa.getRole().getId();

    return User.with(
        jpa.getId(),
        jpa.getName(),
        jpa.getPassword(),
        UserStatus.valueOf(jpa.getStatus()),
        jpa.getCreatedAt(),
        jpa.getUpdatedAt(),
        roleId
    );
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

  public static UserResponse entityToResponse(final User entity) {
    return new UserResponse(
        entity.getId().toString(),
        entity.getName(),
        entity.getStatus().toString(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getRoleId().toString()
    );
  }

  public static UserDetailsResponse jpaToDetailsResponse(final UserJpaEntity jpa) {
    final var role = RoleMapper.jpaToResponse(jpa.getRole());

    return new UserDetailsResponse(
        jpa.getId().toString(),
        jpa.getName(),
        jpa.getStatus(),
        jpa.getCreatedAt(),
        jpa.getUpdatedAt(),
        role
    );
  }
}
