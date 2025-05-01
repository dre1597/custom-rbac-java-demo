package org.example.customrbacjavademo.apps.auth.usecase.mappers;

import org.example.customrbacjavademo.apps.auth.domain.entities.RefreshToken;
import org.example.customrbacjavademo.apps.auth.infra.persistence.RefreshTokenJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;

public final class RefreshTokenMapper {
  private RefreshTokenMapper() {
  }

  public static RefreshTokenJpaEntity entityToJpa(final RefreshToken entity) {
    final var user = entity.userId();
    final var userJpa = new UserJpaEntity();
    userJpa.setId(user);

    return new RefreshTokenJpaEntity(
        entity.id(),
        entity.token(),
        entity.expiryDate(),
        entity.createdAt(),
        entity.updatedAt(),
        userJpa
    );
  }
}
