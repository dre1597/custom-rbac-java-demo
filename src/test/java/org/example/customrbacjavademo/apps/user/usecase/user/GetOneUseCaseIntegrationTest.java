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
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
class GetOneUseCaseIntegrationTest {
  @Autowired
  private GetOneUserUseCase useCase;

  @Autowired
  private UserJpaRepository repository;

  @Autowired
  private RoleJpaRepository roleRepository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Test
  void shouldGetUserById() {
    final var permission = permissionRepository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var role = roleRepository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permission.getId()))));
    final var user = repository.save(UserMapper.entityToJpa(UserTestMocks.createActiveTestUser(role.getId())));

    final var result = useCase.execute(user.getId().toString());

    assertNotNull(result);
    assertEquals(user.getId().toString(), result.id());
    assertEquals(user.getName(), result.name());
    assertEquals(user.getRole().getId().toString(), result.role().id());
    assertEquals(user.getRole().getName(), result.role().name());
    assertEquals(user.getRole().getDescription(), result.role().description());
    assertEquals(user.getRole().getStatus(), result.role().status());
    assertEquals(user.getRole().getCreatedAt(), result.role().createdAt());
    assertEquals(user.getRole().getUpdatedAt(), result.role().updatedAt());
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {
    final var id = UUID.randomUUID().toString();
    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id));
    assertEquals("User not found", exception.getMessage());
  }

  @Test
  void shouldThrowValidationExceptionWhenIdIsNotAValidUUID() {
    final var id = "invalid_uuid";
    final var exception = assertThrows(ValidationException.class, () -> useCase.execute(id));
    assertEquals("Invalid UUID: invalid_uuid", exception.getMessage());
  }
}
