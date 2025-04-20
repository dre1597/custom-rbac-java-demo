package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.UserTestMocks;
import org.example.customrbacjavademo.apps.user.domain.services.PasswordService;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
class UpdatePasswordUseCaseIntegrationTest {
  @Autowired
  private UpdatePasswordUseCase useCase;

  @Autowired
  private UserJpaRepository repository;

  @Autowired
  private RoleJpaRepository roleRepository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Test
  void shouldUpdatePassword() {
    final var permission = permissionRepository.save(
        PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission())
    );
    final var role = roleRepository.save(
        RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId())))
    );
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var newPassword = "updated_password";
    useCase.execute(user.getId().toString(), newPassword);

    final var updatedUser = repository.findById(user.getId()).orElseThrow();
    assertNotNull(updatedUser.getPassword());
    assertTrue(PasswordService.matches(newPassword, updatedUser.getPassword()));
    assertNotEquals(user.getPassword(), updatedUser.getPassword());
  }

  @Test
  void shouldThrowWhenUserNotFound() {
    final var nonExistentId = UUID.randomUUID();

    final var exception = assertThrows(
        NotFoundException.class,
        () -> useCase.execute(nonExistentId.toString(), "new_password")
    );

    assertEquals("User not found", exception.getMessage());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  void shouldThrowWhenPasswordIsInvalid(final String invalidPassword) {
    final var permission = permissionRepository.save(
        PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission())
    );
    final var role = roleRepository.save(
        RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId())))
    );
    final var user = repository.save(
        UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId()))
    );

    final var exception = assertThrows(
        ValidationException.class,
        () -> useCase.execute(user.getId().toString(), invalidPassword)
    );

    assertEquals("password is required", exception.getMessage());
  }
}
