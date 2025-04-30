package org.example.customrbacjavademo.apps.auth.usecase;

import org.example.customrbacjavademo.apps.auth.domain.dto.LoginDto;
import org.example.customrbacjavademo.apps.auth.domain.entities.RefreshToken;
import org.example.customrbacjavademo.apps.auth.domain.services.JwtService;
import org.example.customrbacjavademo.apps.auth.domain.services.RefreshTokenService;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.responses.LoginResponse;
import org.example.customrbacjavademo.apps.auth.infra.persistence.RefreshTokenJpaRepository;
import org.example.customrbacjavademo.apps.auth.usecase.mappers.AuthMapper;
import org.example.customrbacjavademo.apps.auth.usecase.mappers.RefreshTokenMapper;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

@Service
public class LoginUseCase {
  private final UserJpaRepository userJpaRepository;
  private final RefreshTokenJpaRepository refreshTokenJpaRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

  public LoginUseCase(
      final UserJpaRepository userJpaRepository,
      final RefreshTokenJpaRepository refreshTokenJpaRepository,
      final AuthenticationManager authenticationManager,
      final JwtService jwtService,
      final RefreshTokenService refreshTokenService
  ) {
    this.userJpaRepository = Objects.requireNonNull(userJpaRepository);
    this.refreshTokenJpaRepository = Objects.requireNonNull(refreshTokenJpaRepository);
    this.authenticationManager = Objects.requireNonNull(authenticationManager);
    this.jwtService = Objects.requireNonNull(jwtService);
    this.refreshTokenService = Objects.requireNonNull(refreshTokenService);
  }

  public LoginResponse execute(final LoginDto input) {
    this.validate(input);

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            input.name(),
            input.password()
        )
    );

    final var user = userJpaRepository.findWithRoleByName(input.name())
        .orElseThrow(() -> new NotFoundException("User not found"));

    final var jwtToken = jwtService.generateToken(user);

    final var refreshJwtToken = refreshTokenService.generateToken(user);
    final var refreshToken = RefreshToken.newRefreshToken(
        refreshJwtToken,
        Instant.now().plusSeconds(3600),
        user.getId()
    );
    
    this.refreshTokenJpaRepository.save(RefreshTokenMapper.entityToJpa(refreshToken));

    return new LoginResponse(AuthMapper.jpaToResponse(user), jwtToken, refreshJwtToken);
  }

  private void validate(final LoginDto input) {
    final var errors = new ArrayList<String>();

    if (input.name() == null || input.name().isBlank()) {
      errors.add("name is required");
    }

    if (input.password() == null || input.password().isBlank()) {
      errors.add("password is required");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }
}
