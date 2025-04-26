package org.example.customrbacjavademo.apps.auth.infra.api.controllers;

import org.example.customrbacjavademo.apps.auth.domain.dto.LoginDto;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.requests.LoginRequest;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.responses.LoginResponse;
import org.example.customrbacjavademo.apps.auth.usecase.LoginUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthAPI {
  private final LoginUseCase loginUseCase;

  public AuthController(final LoginUseCase loginUseCase) {
    this.loginUseCase = loginUseCase;
  }

  @Override
  public ResponseEntity<LoginResponse> login(final LoginRequest input) {
    final var dto = LoginDto.from(input);
    return ResponseEntity.ok(loginUseCase.execute(dto));
  }
}
