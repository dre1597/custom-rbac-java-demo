package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.IntegrationTest;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
class DeleteUserUseCaseIntegrationTest {
  @Autowired
  private DeleteUserUseCase useCase;

  @Autowired
  private UserJpaRepository repository;

  @Autowired
  private RoleJpaRepository roleRepository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Test
  void shouldDeleteUser() {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    useCase.execute(user.getId().toString());

    final var exists = repository.existsById(user.getId());
    assertFalse(exists);
  }

  @Test
  void shouldDoNothingWhenUserDoesNotExist() {
    final var id = UUID.randomUUID();
    assertDoesNotThrow(() -> useCase.execute(id.toString()));
  }

  @Test
  void shouldThrowValidationExceptionWhenIdIsNotAValidUUID() {
    final var id = "invalid_uuid";
    final var exception = assertThrows(ValidationException.class, () -> useCase.execute(id));
    assertEquals("Invalid UUID: invalid_uuid", exception.getMessage());
  }
}
