package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
class GetOneRoleUseCaseIntegrationTest {
  @Autowired
  private GetOneRoleUseCase useCase;

  @Autowired
  private RoleJpaRepository repository;

  @Autowired
  private PermissionJpaRepository permissionRepository;

  @Test
  void shouldGetRoleById() {
    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var permissionJpa = permissionRepository.save(PermissionMapper.entityToJpa(permission));
    final var role = repository.save(RoleMapper.entityToJpa(RoleTestMocks.createActiveTestRole(List.of(permissionJpa.getId()))));

    final var result = useCase.execute(role.getId().toString());

    assertEquals(role.getId(), result.id());
    assertEquals(role.getName(), result.name());
    assertEquals(role.getDescription(), result.description());
    assertEquals(role.getStatus(), result.status());
    assertEquals(role.getPermissions().size(), result.permissions().size());
    assertNotNull(result.createdAt());
    assertNotNull(result.updatedAt());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenRoleDoesNotExist() {
    final var id = UUID.randomUUID().toString();
    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id));
    assertEquals("Role not found", exception.getMessage());
  }

  @Test
  void shouldThrowValidationExceptionWhenIdIsNotAValidUUID() {
    final var id = "invalid_uuid";

    final var exception = assertThrows(ValidationException.class, () -> useCase.execute(id));

    assertEquals("Invalid UUID: invalid_uuid", exception.getMessage());
  }
}
