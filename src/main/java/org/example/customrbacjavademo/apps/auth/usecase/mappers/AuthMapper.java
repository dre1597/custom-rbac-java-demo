package org.example.customrbacjavademo.apps.auth.usecase.mappers;

import org.example.customrbacjavademo.apps.auth.infra.api.dto.responses.LoginResponse;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;

public final class AuthMapper {
  private AuthMapper() {
  }

  public static LoginResponse jpaToResponse(final UserJpaEntity jpa) {
    return new LoginResponse(
        jpa.getId().toString(),
        jpa.getName(),
        jpa.getRole().getId().toString(),
        jpa.getRole().getName()
    );
  }
}
