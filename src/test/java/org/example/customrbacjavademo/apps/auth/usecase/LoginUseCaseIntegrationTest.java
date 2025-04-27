package org.example.customrbacjavademo.apps.auth.usecase;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.auth.domain.dto.LoginDto;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
class LoginUseCaseIntegrationTest {
  @Autowired
  private LoginUseCase useCase;

  @Autowired
  private UserJpaRepository userRepository;

  @Autowired
  private RoleJpaRepository roleRepository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Test
  void shouldLogin() {
    final var permission = permissionRepository.save(
        PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission())
    );
    final var role = roleRepository.save(
        RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId())))
    );
    final var user = userRepository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));
    final var dto = LoginDto.of(user.getName(), "any_password");

    final var response = useCase.execute(dto);

    assertNotNull(response.token());
    assertEquals(user.getId().toString(), response.user().id());
    assertEquals(user.getName(), response.user().name());
    assertEquals(user.getRole().getId().toString(), response.user().roleId());
    assertEquals(user.getRole().getName(), response.user().roleName());
  }

  @Test
  void shouldThrowWhenAuthenticationFails() {
    final var permission = permissionRepository.save(
        PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission())
    );
    final var role = roleRepository.save(
        RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId())))
    );
    final var user = userRepository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var dto = LoginDto.of(user.getName(), "invalid_password");

    final var exception = assertThrows(
        AuthenticationException.class,
        () -> useCase.execute(dto)
    );

    assertEquals("Bad credentials", exception.getMessage());
  }

  @Test
  void shouldThrowWhenUserNotFoundAfterAuthentication() {
    final var loginDto = new LoginDto("authenticated_but_not_found", "password");

    final var exception = assertThrows(AuthenticationException.class,
        () -> useCase.execute(loginDto));

    assertEquals("Bad credentials", exception.getMessage());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowWhenUsernameIsEmpty(final String username) {
    final var loginDto = new LoginDto(username, "password");

    final var exception = assertThrows(ValidationException.class,
        () -> useCase.execute(loginDto));

    assertEquals("name is required", exception.getMessage());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowWhenPasswordIsEmpty(final String password) {
    final var loginDto = new LoginDto("username", password);

    final var exception = assertThrows(ValidationException.class,
        () -> useCase.execute(loginDto));

    assertEquals("password is required", exception.getMessage());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowWhenUsernameAndPasswordAreEmpty(final String input) {
    final var loginDto = new LoginDto(input, input);

    final var exception = assertThrows(ValidationException.class,
        () -> useCase.execute(loginDto));

    assertEquals("name is required, password is required", exception.getMessage());
  }
}
