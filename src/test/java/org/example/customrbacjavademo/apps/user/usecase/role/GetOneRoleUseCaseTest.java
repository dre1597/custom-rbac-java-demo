package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
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
class GetOneRoleUseCaseTest {
  @Mock
  private RoleJpaRepository repository;

  @InjectMocks
  private GetOneRoleUseCase useCase;

  @Test
  void shouldGetRoleById() {
    final var id = UUID.randomUUID();
    final var role = RoleTestMocks.createActiveTestRoleJpa();

    when(repository.findWithPermissionsById(id))
        .thenReturn(Optional.of(role));

    final var result = useCase.execute(id);
    final var permissions = role.getPermissions().stream().map(PermissionMapper::jpaToResponse).toList();

    assertNotNull(result);
    assertEquals(role.getId(), result.id());
    assertEquals(role.getName(), result.name());
    assertEquals(role.getDescription(), result.description());
    assertEquals(role.getStatus(), result.status());
    assertEquals(role.getCreatedAt(), result.createdAt());
    assertEquals(role.getUpdatedAt(), result.updatedAt());
    assertEquals(permissions, result.permissions());
  }

  @Test
  void shouldThrowExceptionWhenRoleNotFound() {
    final var id = UUID.randomUUID();

    when(repository.findWithPermissionsById(id))
        .thenReturn(Optional.empty());

    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id));

    verify(repository, times(1)).findWithPermissionsById(id);
    assertEquals("Role not found", exception.getMessage());
  }
}
