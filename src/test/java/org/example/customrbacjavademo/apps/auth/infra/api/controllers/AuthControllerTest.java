package org.example.customrbacjavademo.apps.auth.infra.api.controllers;

import org.example.customrbacjavademo.apps.auth.domain.dto.LoginDto;
import org.example.customrbacjavademo.apps.auth.domain.dto.RefreshTokenDto;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.requests.LoginRequest;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.requests.RefreshTokenRequest;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.responses.LoginResponse;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.responses.UserLoginResponse;
import org.example.customrbacjavademo.apps.auth.usecase.LoginUseCase;
import org.example.customrbacjavademo.apps.auth.usecase.RefreshTokenUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
  @Mock
  private LoginUseCase loginUseCase;

  @Mock
  private RefreshTokenUseCase refreshTokenUseCase;

  @InjectMocks
  private AuthController controller;

  @Test
  void shouldLogin() {
    final var input = new LoginRequest("any_name", "any_password");
    final var userLoginResponse = new UserLoginResponse("any_id", "any_name", "any_role_id", "any_role_name");
    final var expectedResponse = new LoginResponse(userLoginResponse, "any_token", "any_refresh_token");

    when(loginUseCase.execute(LoginDto.from(input))).thenReturn(expectedResponse);
    controller.login(input);

    verify(loginUseCase).execute(LoginDto.from(input));
  }

  @Test
  void shouldRefresh() {
    final var input = new RefreshTokenRequest("any_refresh_token");
    final var expectedResponse = new LoginResponse(new UserLoginResponse("any_id", "any_name", "any_role_id", "any_role_name"), "any_token", "any_refresh_token");

    when(refreshTokenUseCase.execute(RefreshTokenDto.from(input))).thenReturn(expectedResponse);
    controller.refresh(input);

    verify(refreshTokenUseCase).execute(RefreshTokenDto.from(input));
  }
}
