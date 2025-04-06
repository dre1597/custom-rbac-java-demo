package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOnePermissionUseCaseTest {
  @Mock
  private PermissionJpaRepository repository;

  @InjectMocks
  private GetOnePermissionUseCase useCase;

  @Test
  void shouldGetPermissionById() {
    final var id = UUID.randomUUID();
    final var permission = PermissionTestMocks.createActiveTestPermission();

    when(repository.findById(id))
        .thenReturn(Optional.of(PermissionMapper.entityToJpa(permission)));

    var result = useCase.execute(id);

    verify(repository, times(1)).findById(id);
    assertNotNull(result);
    assertEquals(permission.getId(), result.id());
    assertEquals(permission.getName().name(), result.name());
    assertEquals(permission.getScope().name(), result.scope());
    assertEquals(permission.getDescription(), result.description());
    assertEquals(permission.getStatus().name(), result.status());
    assertEquals(permission.getCreatedAt(), result.createdAt());
    assertEquals(permission.getUpdatedAt(), result.updatedAt());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenPermissionDoesNotExist() {
    final var id = UUID.randomUUID();

    when(repository.findById(id))
        .thenReturn(Optional.empty());

    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id));

    verify(repository, times(1)).findById(id);
    assertEquals("Permission not found", exception.getMessage());
  }

}
