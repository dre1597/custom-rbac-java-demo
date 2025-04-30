package org.example.customrbacjavademo.apps.auth.usecase;

import org.example.customrbacjavademo.apps.auth.domain.dto.LoginDto;
import org.example.customrbacjavademo.apps.auth.domain.services.JwtService;
import org.example.customrbacjavademo.apps.auth.domain.services.RefreshTokenService;
import org.example.customrbacjavademo.apps.auth.infra.persistence.RefreshTokenJpaRepository;
import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {
  @Mock
  private UserJpaRepository userJpaRepository;

  @Mock
  private RefreshTokenJpaRepository refreshTokenJpaRepository;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtService jwtService;

  @Mock
  private RefreshTokenService refreshTokenService;

  @InjectMocks
  private LoginUseCase useCase;

  @Test
  void shouldLogin() {
    final var password = "any_password";
    final var user = UserTestMocks.createActiveTestUser("any_name", password);

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(new UsernamePasswordAuthenticationToken(user.getName(), password));
    when(userJpaRepository.findWithRoleByName(user.getName()))
        .thenReturn(Optional.of(UserMapper.entityToJpa(user)));

    final var result = useCase.execute(new LoginDto(user.getName(), password));

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userJpaRepository).findWithRoleByName(user.getName());

    assertEquals(user.getId().toString(), result.user().id());
    assertEquals(user.getName(), result.user().name());
  }

  @Test
  void shouldThrowWhenAuthenticationFails() {
    final var loginDto = new LoginDto("invalid_user", "wrong_password");

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Bad credentials"));

    assertThrows(BadCredentialsException.class, () -> useCase.execute(loginDto));

    verify(authenticationManager).authenticate(
        new UsernamePasswordAuthenticationToken("invalid_user", "wrong_password")
    );
    verify(userJpaRepository, never()).findWithRoleByName(anyString());
  }

  @Test
  void shouldThrowWhenUserNotFoundAfterAuthentication() {
    final var loginDto = new LoginDto("authenticated_but_not_found", "password");

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(null);
    when(userJpaRepository.findWithRoleByName("authenticated_but_not_found"))
        .thenReturn(Optional.empty());

    final var exception = assertThrows(NotFoundException.class,
        () -> useCase.execute(loginDto));

    assertEquals("User not found", exception.getMessage());

    verify(authenticationManager).authenticate(
        new UsernamePasswordAuthenticationToken("authenticated_but_not_found", "password")
    );
    verify(userJpaRepository).findWithRoleByName("authenticated_but_not_found");
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowWhenUsernameIsEmpty(final String username) {
    final var loginDto = new LoginDto(username, "password");

    final var exception = assertThrows(ValidationException.class,
        () -> useCase.execute(loginDto));

    assertEquals("name is required", exception.getMessage());

    verify(authenticationManager, never()).authenticate(any());
    verify(userJpaRepository, never()).findWithRoleByName(any());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowWhenPasswordIsEmpty(final String password) {
    final var loginDto = new LoginDto("username", password);

    final var exception = assertThrows(ValidationException.class,
        () -> useCase.execute(loginDto));

    assertEquals("password is required", exception.getMessage());

    verify(authenticationManager, never()).authenticate(any());
    verify(userJpaRepository, never()).findWithRoleByName(any());
  }

  @Test
  void shouldThrowWhenUsernameAndPasswordAreNull() {
    final var loginDto = new LoginDto(null, null);

    final var exception = assertThrows(ValidationException.class,
        () -> useCase.execute(loginDto));

    assertEquals("name is required, password is required", exception.getMessage());

    verify(authenticationManager, never()).authenticate(any());
    verify(userJpaRepository, never()).findWithRoleByName(any());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowWhenUsernameAndPasswordAreEmpty(final String input) {
    final var loginDto = new LoginDto(input, input);

    final var exception = assertThrows(ValidationException.class,
        () -> useCase.execute(loginDto));

    assertEquals("name is required, password is required", exception.getMessage());

    verify(authenticationManager, never()).authenticate(any());
    verify(userJpaRepository, never()).findWithRoleByName(any());
  }
}
