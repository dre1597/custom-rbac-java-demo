package org.example.customrbacjavademo.apps.user.usecase;

import org.example.customrbacjavademo.apps.user.domain.dto.UpdatePermissionDto;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.PermissionTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.mappers.PermissionMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    var id = UUID.randomUUID();
    var dto = UpdatePermissionDto.of(PermissionName.READ, PermissionScope.USER, "any_description", PermissionStatus.INACTIVE);

    var permission = PermissionTestMocks.createActiveTestPermission();

    when(repository.findById(id)).thenReturn(Optional.of(PermissionMapper.entityToJpa(permission)));
    when(repository.existsByNameAndScope(dto.name().toString(), dto.scope().toString())).thenReturn(false);

    useCase.execute(id, dto);

    var permissionJpaEntityCaptor = ArgumentCaptor.forClass(PermissionJpaEntity.class);
    verify(repository, times(1)).save(permissionJpaEntityCaptor.capture());
    var capturedPermission = permissionJpaEntityCaptor.getValue();

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
    var id = UUID.randomUUID();
    var dto = UpdatePermissionDto.of(PermissionName.READ, PermissionScope.USER, "new_description", PermissionStatus.INACTIVE);

    when(repository.findById(id)).thenReturn(Optional.empty());

    var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id, dto));
    assertEquals("Permission not found", exception.getMessage());
  }

  @Test
  void shouldNotUpdateToDuplicateNameAndScope() {
    var id = UUID.randomUUID();
    var dto = UpdatePermissionDto.of(PermissionName.READ, PermissionScope.USER, "new_description", PermissionStatus.ACTIVE);

    var permission = PermissionTestMocks.createActiveTestPermission();

    when(repository.findById(id)).thenReturn(Optional.of(PermissionMapper.entityToJpa(permission)));
    when(repository.existsByNameAndScope(dto.name().toString(), dto.scope().toString())).thenReturn(true);

    var exception = assertThrows(AlreadyExistsException.class, () -> useCase.execute(id, dto));
    assertEquals("Permission already exists", exception.getMessage());
  }
}