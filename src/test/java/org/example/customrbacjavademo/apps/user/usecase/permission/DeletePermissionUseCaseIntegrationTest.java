package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.IntegrationTest;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
class DeletePermissionUseCaseIntegrationTest {
  @Autowired
  private DeletePermissionUseCase useCase;

  @Autowired
  private PermissionJpaRepository repository;

  @Test
  void shouldDeletePermission() {
    final var permission = repository.save(PermissionMapper.entityToJpa(PermissionTestMocks.createActiveTestPermission()));

    useCase.execute(permission.getId().toString());

    final var exists = repository.existsById(permission.getId());
    assertFalse(exists);
  }

  @Test
  void shouldDoNothingWhenMangaDoesNotExist() {
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
