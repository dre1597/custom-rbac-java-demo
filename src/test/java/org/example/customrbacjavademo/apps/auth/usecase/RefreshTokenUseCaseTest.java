package org.example.customrbacjavademo.apps.auth.usecase;

import org.example.customrbacjavademo.apps.auth.domain.dto.RefreshTokenDto;
import org.example.customrbacjavademo.apps.auth.domain.mocks.RefreshTokenTestMocks;
import org.example.customrbacjavademo.apps.auth.domain.services.JwtService;
import org.example.customrbacjavademo.apps.auth.domain.services.RefreshTokenService;
import org.example.customrbacjavademo.apps.auth.infra.persistence.RefreshTokenJpaRepository;
import org.example.customrbacjavademo.apps.auth.usecase.mappers.RefreshTokenMapper;
import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.exceptions.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {
  @Mock
  private RefreshTokenJpaRepository refreshTokenJpaRepository;

  @Mock
  private UserJpaRepository userJpaRepository;

  @Mock
  private RefreshTokenService refreshTokenService;

  @Mock
  private JwtService jwtService;

  @InjectMocks
  private RefreshTokenUseCase useCase;

  @Test
  void shouldRefreshToken() {
    final var user = UserTestMocks.createActiveTestUser("any_name", "any_password");
    final var refreshTokenValue = "any_refresh_token";
    final var refreshToken = RefreshTokenTestMocks.createActiveTestRefreshToken(refreshTokenValue, user.getId());
    final var refreshTokenJpa = RefreshTokenMapper.entityToJpa(refreshToken);
    final var userJpa = UserMapper.entityToJpa(user);

    when(refreshTokenJpaRepository.findWithUserByToken(refreshTokenValue))
        .thenReturn(Optional.of(refreshTokenJpa));
    when(refreshTokenService.isTokenValid(refreshTokenValue))
        .thenReturn(true);
    when(userJpaRepository.findById(user.getId()))
        .thenReturn(Optional.of(userJpa));
    when(jwtService.generateToken(userJpa)).thenReturn("new_jwt_token");
    doReturn("new_refresh_token")
        .when(refreshTokenService)
        .generateToken(argThat(u -> {
          if (!(u instanceof UserJpaEntity)) return false;
          return ((UserJpaEntity) u).getId().equals(userJpa.getId());
        }));


    final var result = useCase.execute(new RefreshTokenDto(refreshTokenValue));

    assertEquals("new_jwt_token", result.token());
    assertEquals("new_refresh_token", result.refreshToken());

    verify(refreshTokenJpaRepository).deleteByUser(userJpa);
    verify(refreshTokenJpaRepository).save(any());
  }

  @Test
  void shouldThrowIfRefreshTokenNotFound() {
    final var dto = new RefreshTokenDto("invalid_token");

    when(refreshTokenJpaRepository.findWithUserByToken(dto.refreshToken()))
        .thenReturn(Optional.empty());

    final var exception = assertThrows(UnauthorizedException.class, () -> useCase.execute(dto));
    assertEquals("Invalid refresh token", exception.getMessage());

    verify(refreshTokenService, never()).isTokenValid(any());
    verify(userJpaRepository, never()).findById(any());
  }

  @Test
  void shouldThrowIfRefreshTokenIsInvalid() {
    final var user = UserTestMocks.createActiveTestUser("name", "pass");
    final var refreshTokenValue = "invalid_token";
    final var refreshToken = RefreshTokenTestMocks.createActiveTestRefreshToken(refreshTokenValue, user.getId());
    final var refreshTokenJpa = RefreshTokenMapper.entityToJpa(refreshToken);

    when(refreshTokenJpaRepository.findWithUserByToken(refreshTokenValue))
        .thenReturn(Optional.of(refreshTokenJpa));
    when(refreshTokenService.isTokenValid(refreshTokenValue))
        .thenReturn(false);

    final var exception = assertThrows(UnauthorizedException.class, () -> useCase.execute(new RefreshTokenDto(refreshTokenValue)));
    assertEquals("Invalid refresh token", exception.getMessage());

    verify(userJpaRepository, never()).findById(any());
  }

  @Test
  void shouldThrowIfUserNotFound() {
    final var user = UserTestMocks.createActiveTestUser("name", "pass");
    final var refreshTokenValue = "valid_token";

    final var refreshToken = RefreshTokenTestMocks.createActiveTestRefreshToken(refreshTokenValue, user.getId());
    final var refreshTokenJpa = RefreshTokenMapper.entityToJpa(refreshToken);

    when(refreshTokenJpaRepository.findWithUserByToken(refreshTokenValue))
        .thenReturn(Optional.of(refreshTokenJpa));
    when(refreshTokenService.isTokenValid(refreshTokenValue))
        .thenReturn(true);
    when(userJpaRepository.findById(user.getId()))
        .thenReturn(Optional.empty());

    final var ex = assertThrows(UnauthorizedException.class, () -> useCase.execute(new RefreshTokenDto(refreshTokenValue)));
    assertEquals("User not found", ex.getMessage());
  }
}
