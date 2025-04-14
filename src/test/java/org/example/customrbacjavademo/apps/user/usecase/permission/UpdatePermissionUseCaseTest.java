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
import org.example.customrbacjavademo.common.domain.exceptions.ValidationException;
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
    final var dto = UpdatePermissionDto.of(PermissionName.READ.toString(), PermissionScope.USER.toString(), "any_description", PermissionStatus.INACTIVE.toString());

    final var permission = PermissionTestMocks.createActiveTestPermission();

    when(repository.findById(id)).thenReturn(Optional.of(PermissionMapper.entityToJpa(permission)));

    useCase.execute(id.toString(), dto);

    final var permissionJpaEntityCaptor = ArgumentCaptor.forClass(PermissionJpaEntity.class);
    verify(repository, times(1)).save(permissionJpaEntityCaptor.capture());
    final var capturedPermission = permissionJpaEntityCaptor.getValue();

    assertNotNull(capturedPermission.getId());
    assertEquals(dto.name(), capturedPermission.getName());
    assertEquals(dto.scope(), capturedPermission.getScope());
    assertEquals(dto.description(), capturedPermission.getDescription());
    assertEquals(dto.status(), capturedPermission.getStatus());
    assertNotNull(capturedPermission.getCreatedAt());
    assertNotNull(capturedPermission.getUpdatedAt());
  }

  @Test
  void shouldNotUpdateNonExistentPermission() {
    final var id = UUID.randomUUID();
    final var dto = UpdatePermissionDto.of(PermissionName.READ.name(), PermissionScope.USER.name(), "new_description", PermissionStatus.INACTIVE.name());

    when(repository.findById(id)).thenReturn(Optional.empty());

    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id.toString(), dto));
    assertEquals("Permission not found", exception.getMessage());
  }

  @Test
  void shouldNotUpdateWithInvalidId() {
    final var id = "invalid_uuid";
    final var dto = UpdatePermissionDto.of(PermissionName.READ.name(), PermissionScope.USER.name(), "new_description", PermissionStatus.INACTIVE.name());

    final var exception = assertThrows(ValidationException.class, () -> useCase.execute(id, dto));
    assertEquals("Invalid UUID: " + id, exception.getMessage());
  }

  @Test
  void shouldNotUpdateToDuplicateNameAndScope() {
    final var dto = UpdatePermissionDto.of(PermissionName.CREATE.name(), PermissionScope.USER.name(), "any_description", PermissionStatus.ACTIVE.name());

    final var permission = PermissionTestMocks.createActiveTestPermission();
    final var id = permission.getId();

    when(repository.findById(id)).thenReturn(Optional.of(PermissionMapper.entityToJpa(permission)));
    when(repository.existsByNameAndScope(dto.name(), dto.scope())).thenReturn(true);

    final var exception = assertThrows(AlreadyExistsException.class, () -> useCase.execute(id.toString(), dto));
    assertEquals("Permission already exists", exception.getMessage());
  }

  @Test
  void shouldUpdateWhenOnlyNameChanged() {
    final var id = UUID.randomUUID();
    final var dto = UpdatePermissionDto.of(PermissionName.CREATE.name(), PermissionScope.USER.name(), "old_description", PermissionStatus.ACTIVE.name());
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

    useCase.execute(id.toString(), dto);

    final var permissionJpaEntityCaptor = ArgumentCaptor.forClass(PermissionJpaEntity.class);
    verify(repository, times(1)).save(permissionJpaEntityCaptor.capture());
    final var capturedPermission = permissionJpaEntityCaptor.getValue();

    assertEquals(dto.name(), capturedPermission.getName());
    assertEquals(existingPermission.getScope(), capturedPermission.getScope());
  }

  @Test
  void shouldUpdateWhenOnlyScopeChanged() {
    final var id = UUID.randomUUID();
    final var dto = UpdatePermissionDto.of(PermissionName.READ.name(), PermissionScope.PROFILE.name(), "new_description", PermissionStatus.ACTIVE.name());
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

    useCase.execute(id.toString(), dto);

    final var permissionJpaEntityCaptor = ArgumentCaptor.forClass(PermissionJpaEntity.class);
    verify(repository, times(1)).save(permissionJpaEntityCaptor.capture());
    final var capturedPermission = permissionJpaEntityCaptor.getValue();

    assertEquals(existingPermission.getName(), capturedPermission.getName());
    assertEquals(dto.scope(), capturedPermission.getScope());
  }
}
