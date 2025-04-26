package org.example.customrbacjavademo.apps.auth.domain.dto;

import org.example.customrbacjavademo.apps.auth.infra.api.dto.requests.LoginRequest;

public record LoginDto(
    String name,
    String password
) {

  public static LoginDto of(final String name, final String password) {
    return new LoginDto(name, password);
  }

  public static LoginDto from(final LoginRequest request) {
    return new LoginDto(
        request.name(),
        request.password()
    );
  }
}
