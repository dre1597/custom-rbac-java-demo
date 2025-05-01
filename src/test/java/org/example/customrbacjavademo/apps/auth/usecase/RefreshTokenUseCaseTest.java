package org.example.customrbacjavademo.apps.auth.usecase;

import org.example.customrbacjavademo.apps.auth.domain.dto.RefreshTokenDto;
import org.example.customrbacjavademo.apps.auth.domain.entities.RefreshToken;
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

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    final var userJpa = UserMapper.entityToJpa(user);

    final var tokenValue = "valid_token";
    final var refreshToken = RefreshToken.newRefreshToken(tokenValue, Instant.now().plusSeconds(3600), user.getId());
    final var refreshTokenJpa = RefreshTokenMapper.entityToJpa(refreshToken);
    refreshTokenJpa.setUser(userJpa);

    when(refreshTokenJpaRepository.findWithUserByToken(tokenValue)).thenReturn(Optional.of(refreshTokenJpa));
    when(userJpaRepository.findById(user.getId())).thenReturn(Optional.of(userJpa));
    when(jwtService.generateToken(userJpa)).thenReturn("new_jwt_token");
    when(refreshTokenService.generateToken()).thenReturn("new_refresh_token");

    final var result = useCase.execute(new RefreshTokenDto(tokenValue));

    assertEquals("new_jwt_token", result.token());
    assertEquals("new_refresh_token", result.refreshToken());
    assertEquals(user.getId().toString(), result.user().id());

    verify(refreshTokenJpaRepository).deleteByUser(userJpa);
    verify(refreshTokenJpaRepository).save(any());
  }

  @Test
  void shouldThrowIfRefreshTokenNotFound() {
    when(refreshTokenJpaRepository.findWithUserByToken("invalid_token"))
        .thenReturn(Optional.empty());

    final var ex = assertThrows(UnauthorizedException.class,
        () -> useCase.execute(new RefreshTokenDto("invalid_token")));

    assertEquals("Invalid refresh token", ex.getMessage());
  }

  @Test
  void shouldThrowIfUserNotFound() {
    final var user = UserTestMocks.createActiveTestUser("any", "any");
    final var tokenValue = "valid_token";

    final var userJpa = new UserJpaEntity();
    userJpa.setId(user.getId());

    final var refreshToken = RefreshToken.newRefreshToken(tokenValue, Instant.now().plusSeconds(3600), user.getId());
    final var refreshTokenJpa = RefreshTokenMapper.entityToJpa(refreshToken);
    refreshTokenJpa.setUser(userJpa);

    when(refreshTokenJpaRepository.findWithUserByToken(tokenValue))
        .thenReturn(Optional.of(refreshTokenJpa));
    when(userJpaRepository.findById(user.getId()))
        .thenReturn(Optional.empty());

    final var ex = assertThrows(UnauthorizedException.class,
        () -> useCase.execute(new RefreshTokenDto(tokenValue)));

    assertEquals("User not found", ex.getMessage());
  }
}
