package org.example.customrbacjavademo.apps.auth.usecase;

import org.example.customrbacjavademo.apps.auth.domain.dto.RefreshTokenDto;
import org.example.customrbacjavademo.apps.auth.domain.entities.RefreshToken;
import org.example.customrbacjavademo.apps.auth.domain.services.JwtService;
import org.example.customrbacjavademo.apps.auth.domain.services.RefreshTokenService;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.responses.LoginResponse;
import org.example.customrbacjavademo.apps.auth.infra.persistence.RefreshTokenJpaRepository;
import org.example.customrbacjavademo.apps.auth.usecase.mappers.AuthMapper;
import org.example.customrbacjavademo.apps.auth.usecase.mappers.RefreshTokenMapper;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.common.domain.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
public class RefreshTokenUseCase {
  private final RefreshTokenJpaRepository refreshTokenJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final RefreshTokenService refreshTokenService;
  private final JwtService jwtService;

  public RefreshTokenUseCase(
      final RefreshTokenJpaRepository refreshTokenJpaRepository,
      final UserJpaRepository userJpaRepository,
      final RefreshTokenService refreshTokenService,
      final JwtService jwtService
  ) {
    this.refreshTokenJpaRepository = Objects.requireNonNull(refreshTokenJpaRepository);
    this.userJpaRepository = Objects.requireNonNull(userJpaRepository);
    this.refreshTokenService = Objects.requireNonNull(refreshTokenService);
    this.jwtService = Objects.requireNonNull(jwtService);
  }

  @Transactional
  public LoginResponse execute(final RefreshTokenDto dto) {
    final var refreshTokenJpa = refreshTokenJpaRepository.findWithUserByToken(dto.refreshToken());

    if (refreshTokenJpa.isEmpty()) {
      throw new UnauthorizedException("Invalid refresh token");
    }

    if (!refreshTokenService.isTokenValid(dto.refreshToken())) {
      throw new UnauthorizedException("Invalid refresh token");
    }

    final var user = userJpaRepository.findById(refreshTokenJpa.get().getUser().getId())
        .orElseThrow(() -> new UnauthorizedException("User not found"));

    this.refreshTokenJpaRepository.deleteByUser(user);

    final var jwtToken = jwtService.generateToken(user);
    final var refreshJwtToken = refreshTokenService.generateToken(refreshTokenJpa.get().getUser());

    final var refreshToken = RefreshToken.newRefreshToken(
        refreshJwtToken,
        Instant.now().plusSeconds(3600),
        user.getId()
    );

    this.refreshTokenJpaRepository.save(RefreshTokenMapper.entityToJpa(refreshToken));

    return new LoginResponse(AuthMapper.jpaToResponse(user), jwtToken, refreshJwtToken);
  }
}
