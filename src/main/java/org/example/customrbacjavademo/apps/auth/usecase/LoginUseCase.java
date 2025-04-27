package org.example.customrbacjavademo.apps.auth.usecase;

import org.example.customrbacjavademo.apps.auth.domain.dto.LoginDto;
import org.example.customrbacjavademo.apps.auth.domain.services.JwtService;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.responses.LoginResponse;
import org.example.customrbacjavademo.apps.auth.usecase.mappers.AuthMapper;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class LoginUseCase {
  private final UserJpaRepository userJpaRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public LoginUseCase(
      final UserJpaRepository userJpaRepository,
      final AuthenticationManager authenticationManager,
      final JwtService jwtService
  ) {
    this.userJpaRepository = Objects.requireNonNull(userJpaRepository);
    this.authenticationManager = Objects.requireNonNull(authenticationManager);
    this.jwtService = Objects.requireNonNull(jwtService);
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

    return new LoginResponse(AuthMapper.jpaToResponse(user), jwtToken);
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
