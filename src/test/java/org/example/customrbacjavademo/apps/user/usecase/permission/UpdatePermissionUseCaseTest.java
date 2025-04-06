package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.apps.user.domain.dto.UpdatePermissionDto;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.permission.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePermissionUseCaseTest {
  @Mock
  private PermissionJpaRepository repository;

  @InjectMocks
  private UpdatePermissionUseCase useCase;

  @Test
  void shouldUpdatePermission() {
    final var id = UUID.randomUUID();
    final var dto = UpdatePermissionDto.of(PermissionName.READ, PermissionScope.USER, "any_description", PermissionStatus.INACTIVE);

    final var permission = PermissionTestMocks.createActiveTestPermission();

    when(repository.findById(id)).thenReturn(Optional.of(PermissionMapper.entityToJpa(permission)));
    when(repository.existsByNameAndScope(dto.name().toString(), dto.scope().toString())).thenReturn(false);

    useCase.execute(id, dto);

    final var permissionJpaEntityCaptor = ArgumentCaptor.forClass(PermissionJpaEntity.class);
    verify(repository, times(1)).save(permissionJpaEntityCaptor.capture());
    final var capturedPermission = permissionJpaEntityCaptor.getValue();

    assertNotNull(capturedPermission.getId());
    assertEquals(dto.name().toString(), capturedPermission.getName());
    assertEquals(dto.scope().toString(), capturedPermission.getScope());
    assertEquals(dto.description(), capturedPermission.getDescription());
    assertEquals(dto.status().toString(), capturedPermission.getStatus());
    assertNotNull(capturedPermission.getCreatedAt());
    assertNotNull(capturedPermission.getUpdatedAt());
  }

  @Test
  void shouldNotUpdateNonExistentPermission() {
    final var id = UUID.randomUUID();
    final var dto = UpdatePermissionDto.of(PermissionName.READ, PermissionScope.USER, "new_description", PermissionStatus.INACTIVE);

    when(repository.findById(id)).thenReturn(Optional.empty());

    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id, dto));
    assertEquals("Permission not found", exception.getMessage());
  }

  @Test
  void shouldNotUpdateToDuplicateNameAndScope() {
    final var id = UUID.randomUUID();
    final var dto = UpdatePermissionDto.of(PermissionName.READ, PermissionScope.USER, "new_description", PermissionStatus.ACTIVE);

    final var permission = PermissionTestMocks.createActiveTestPermission();

    when(repository.findById(id)).thenReturn(Optional.of(PermissionMapper.entityToJpa(permission)));
    when(repository.existsByNameAndScope(dto.name().toString(), dto.scope().toString())).thenReturn(true);

    final var exception = assertThrows(AlreadyExistsException.class, () -> useCase.execute(id, dto));
    assertEquals("Permission already exists", exception.getMessage());
  }

  @Test
  void shouldUpdateWhenOnlyNameChanged() {
    final var id = UUID.randomUUID();
    final var dto = UpdatePermissionDto.of(PermissionName.CREATE, PermissionScope.USER, "old_description", PermissionStatus.ACTIVE);
    final var existingPermission = new PermissionJpaEntity(
        id,
        PermissionName.READ.name(),
        PermissionScope.USER.name(),
        "old_description",
        PermissionStatus.ACTIVE.name(),
        Instant.now(),
        Instant.now()
    );

    when(repository.findById(id)).thenReturn(Optional.of(existingPermission));
    when(repository.existsByNameAndScope(dto.name().toString(), existingPermission.getScope()))
        .thenReturn(false);

    useCase.execute(id, dto);

    final var permissionJpaEntityCaptor = ArgumentCaptor.forClass(PermissionJpaEntity.class);
    verify(repository, times(1)).save(permissionJpaEntityCaptor.capture());
    final var capturedPermission = permissionJpaEntityCaptor.getValue();

    assertEquals(dto.name().toString(), capturedPermission.getName());
    assertEquals(existingPermission.getScope(), capturedPermission.getScope());
  }

  @Test
  void shouldUpdateWhenOnlyScopeChanged() {
    final var id = UUID.randomUUID();
    final var dto = UpdatePermissionDto.of(PermissionName.READ, PermissionScope.PROFILE, "new_description", PermissionStatus.ACTIVE);
    final var existingPermission = new PermissionJpaEntity(
        id,
        PermissionName.READ.name(),
        PermissionScope.USER.name(),
        "old_description",
        PermissionStatus.ACTIVE.name(),
        Instant.now(),
        Instant.now()
    );

    when(repository.findById(id)).thenReturn(Optional.of(existingPermission));
    when(repository.existsByNameAndScope(existingPermission.getName(), dto.scope().toString()))
        .thenReturn(false);

    useCase.execute(id, dto);

    final var permissionJpaEntityCaptor = ArgumentCaptor.forClass(PermissionJpaEntity.class);
    verify(repository, times(1)).save(permissionJpaEntityCaptor.capture());
    final var capturedPermission = permissionJpaEntityCaptor.getValue();

    assertEquals(existingPermission.getName(), capturedPermission.getName());
    assertEquals(dto.scope().toString(), capturedPermission.getScope());
  }
}
