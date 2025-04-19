package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRoleUseCaseTest {

  @Mock
  private RoleJpaRepository repository;

  @Mock
  private PermissionJpaRepository permissionRepository;

  @InjectMocks
  private CreateRoleUseCase useCase;

  @Test
  void shouldCreateRole() {
    final var permissionId1 = UUID.randomUUID().toString();
    final var permissionId2 = UUID.randomUUID().toString();
    final var dto = new NewRoleDto("any_name", "any_description", RoleStatus.ACTIVE.name(), List.of(permissionId1, permissionId2));

    when(repository.existsByName(dto.name())).thenReturn(false);
    when(permissionRepository.countByIdIn(dto.permissionIds().stream().map(UUID::fromString).toList())).thenReturn(2L);
    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    useCase.execute(dto);

    final var roleCaptor = ArgumentCaptor.forClass(RoleJpaEntity.class);
    verify(repository, times(1)).save(roleCaptor.capture());
    final var savedRole = roleCaptor.getValue();

    assertEquals(dto.name(), savedRole.getName());
    assertEquals(dto.description(), savedRole.getDescription());
    assertEquals(dto.status(), savedRole.getStatus());
    assertEquals(2, savedRole.getPermissions().size());
    assertNotNull(savedRole.getCreatedAt());
    assertNotNull(savedRole.getUpdatedAt());
  }

  @Test
  void shouldNotCreateRoleIfNameAlreadyExists() {
    final var dto = new NewRoleDto("any_name", "any_description", RoleStatus.ACTIVE.name(), List.of(UUID.randomUUID().toString()));

    when(repository.existsByName(dto.name())).thenReturn(true);

    var exception = assertThrows(AlreadyExistsException.class, () -> useCase.execute(dto));

    assertEquals("Role already exists", exception.getMessage());
    verify(repository, never()).save(any());
  }

  @Test
  void shouldThrowIfSomePermissionsAreInvalid() {
    final var validId = UUID.randomUUID().toString();
    final var invalidId = UUID.randomUUID().toString();
    final var dto = new NewRoleDto("any_name", "any_descriptions", RoleStatus.ACTIVE.name(), List.of(validId, invalidId));

    when(repository.existsByName(dto.name())).thenReturn(false);
    when(permissionRepository.countByIdIn(dto.permissionIds().stream().map(UUID::fromString).toList())).thenReturn(1L);

    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(dto));

    assertEquals("Some permissions are invalid or missing. Provided: " + dto.permissionIds(), exception.getMessage());
    verify(repository, never()).save(any());
  }
}
