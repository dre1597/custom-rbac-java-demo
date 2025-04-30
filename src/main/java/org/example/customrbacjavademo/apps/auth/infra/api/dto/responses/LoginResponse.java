package org.example.customrbacjavademo.apps.auth.infra.api.dto.responses;

public record LoginResponse(
    UserLoginResponse user,
    String token,
    String refreshToken
) {
}

