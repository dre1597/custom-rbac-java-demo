package org.example.customrbacjavademo.apps.auth.domain.dto;

import org.example.customrbacjavademo.apps.auth.infra.api.dto.requests.RefreshTokenRequest;

public record RefreshTokenDto(
    String refreshToken
) {
  public static RefreshTokenDto of(final String refreshToken) {
    return new RefreshTokenDto(refreshToken);
  }

  public static RefreshTokenDto from(final RefreshTokenRequest request) {
    return new RefreshTokenDto(request.refreshToken());
  }
}
