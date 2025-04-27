package org.example.customrbacjavademo.apps.auth.usecase.mappers;

import org.example.customrbacjavademo.apps.auth.infra.api.dto.responses.UserLoginResponse;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;

public final class AuthMapper {
  private AuthMapper() {
  }

  public static UserLoginResponse jpaToResponse(final UserJpaEntity jpa) {
    return new UserLoginResponse(
        jpa.getId().toString(),
        jpa.getName(),
        jpa.getRole().getId().toString(),
        jpa.getRole().getName()
    );
  }
}
