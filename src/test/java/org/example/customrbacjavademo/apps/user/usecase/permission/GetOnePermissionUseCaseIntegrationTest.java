package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
class GetOnePermissionUseCaseIntegrationTest {
  @Autowired
  private GetOnePermissionUseCase useCase;

  @Autowired
  private PermissionJpaRepository repository;

  @Test
  void shouldGetPermissionById() {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));
    final var result = useCase.execute(permission.getId().toString());
    final var permissionResponse = PermissionMapper.jpaToResponse(permission);

    assertEquals(permissionResponse.name(), result.name());
    assertEquals(permissionResponse.scope(), result.scope());
    assertEquals(permissionResponse.description(), result.description());
    assertEquals(permissionResponse.status(), result.status());
    assertNotNull(result.createdAt());
    assertNotNull(result.updatedAt());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenPermissionDoesNotExist() {
    final var id = UUID.randomUUID().toString();
    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id));
    assertEquals("Permission not found", exception.getMessage());
  }

  @Test
  void shouldThrowValidationExceptionWhenIdIsNotAValidUUID() {
    final var id = "invalid_uuid";

    final var exception = assertThrows(ValidationException.class, () -> useCase.execute(id));

    assertEquals("Invalid UUID: invalid_uuid", exception.getMessage());
  }
}
