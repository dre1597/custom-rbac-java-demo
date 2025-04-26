package org.example.customrbacjavademo.apps.auth.usecase;

import org.example.customrbacjavademo.apps.auth.domain.dto.LoginDto;
import org.example.customrbacjavademo.apps.auth.infra.api.dto.responses.LoginResponse;
import org.example.customrbacjavademo.apps.auth.usecase.mappers.AuthMapper;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LoginUseCase {
  private final UserJpaRepository userJpaRepository;
  private final AuthenticationManager authenticationManager;

  public LoginUseCase(
      final UserJpaRepository userJpaRepository,
      final AuthenticationManager authenticationManager
  ) {
    this.userJpaRepository = Objects.requireNonNull(userJpaRepository);
    this.authenticationManager = Objects.requireNonNull(authenticationManager);
  }

  public LoginResponse execute(final LoginDto input) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            input.name(),
            input.password()
        )
    );

    return userJpaRepository.findWithRoleByName(input.name())
        .map(AuthMapper::jpaToResponse)
        .orElseThrow(() -> new NotFoundException("User not found"));
  }
}
