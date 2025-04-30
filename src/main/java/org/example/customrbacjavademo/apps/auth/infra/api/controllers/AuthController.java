package org.example.customrbacjavademo.apps.auth.infra.api.controllers;

import org.example.customrbacjavademo.apps.auth.domain.dto.LoginDto;
import org.example.customrbacjavademo.apps.auth.domain.dto.RefreshTokenDto;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.requests.LoginRequest;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.requests.RefreshTokenRequest;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.responses.LoginResponse;
import org.example.customrbacjavademo.apps.auth.usecase.LoginUseCase;
import org.example.customrbacjavademo.apps.auth.usecase.RefreshTokenUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class AuthController implements AuthAPI {
  private final LoginUseCase loginUseCase;
  private final RefreshTokenUseCase refreshTokenUseCase;

  public AuthController(
      final LoginUseCase loginUseCase,
      final RefreshTokenUseCase refreshTokenUseCase
  ) {
    this.loginUseCase = Objects.requireNonNull(loginUseCase);
    this.refreshTokenUseCase = Objects.requireNonNull(refreshTokenUseCase);
  }

  @Override
  public ResponseEntity<LoginResponse> login(final LoginRequest input) {
    final var dto = LoginDto.from(input);
    return ResponseEntity.ok(loginUseCase.execute(dto));
  }

  @Override
  public ResponseEntity<LoginResponse> refresh(final RefreshTokenRequest input) {
    final var dto = RefreshTokenDto.from(input);
    return ResponseEntity.ok(refreshTokenUseCase.execute(dto));
  }
}
