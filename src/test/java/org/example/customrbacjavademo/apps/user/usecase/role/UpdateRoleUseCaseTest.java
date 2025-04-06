package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.domain.dto.UpdateRoleDto;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;
import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.InvalidReferenceException;
import org.example.customrbacjavademo.common.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateRoleUseCaseTest {
  @Mock
  private RoleJpaRepository repository;

  @Mock
  private PermissionJpaRepository permissionRepository;

  @InjectMocks
  private UpdateRoleUseCase useCase;

  @Test
  void shouldUpdateRole() {
    final var id = UUID.randomUUID();
    final var permissionIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    final var dto = UpdateRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE, permissionIds);

    final var role = RoleTestMocks.createActiveTestRole(permissionIds);

    when(repository.findById(id)).thenReturn(Optional.of(RoleMapper.entityToJpa(role)));
    when(permissionRepository.countByIdIn(dto.permissionIds())).thenReturn(2L);

    useCase.execute(id, dto);

    final var roleJpaEntityCaptor = ArgumentCaptor.forClass(RoleJpaEntity.class);
    verify(repository, times(1)).save(roleJpaEntityCaptor.capture());
    final var capturedRole = roleJpaEntityCaptor.getValue();

    assertEquals(dto.name(), capturedRole.getName());
    assertEquals(dto.description(), capturedRole.getDescription());
    assertEquals(dto.status().toString(), capturedRole.getStatus());
    assertEquals(2, capturedRole.getPermissions().size());
    assertTrue(capturedRole.getPermissions().stream().allMatch(p -> dto.permissionIds().contains(p.getId())));
    assertNotNull(capturedRole.getCreatedAt());
    assertNotNull(capturedRole.getUpdatedAt());
  }

  @Test
  void shouldNotUpdateNonExistentRole() {
    final var id = UUID.randomUUID();
    final var permissionIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    final var dto = UpdateRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE, permissionIds);

    when(repository.findById(id)).thenReturn(Optional.empty());

    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id, dto));
    assertEquals("Role not found", exception.getMessage());
  }

  @Test
  void shouldNotUpdateNoneExistentPermissions() {
    final var id = UUID.randomUUID();
    final var permissionIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    final var dto = UpdateRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE, permissionIds);

    final var role = RoleTestMocks.createActiveTestRole(permissionIds);

    when(repository.findById(id)).thenReturn(Optional.of(RoleMapper.entityToJpa(role)));
    when(permissionRepository.countByIdIn(dto.permissionIds())).thenReturn(0L);

    final var exception = assertThrows(InvalidReferenceException.class, () -> useCase.execute(id, dto));
    assertEquals("Some permissions are invalid or missing. Provided: " + dto.permissionIds(), exception.getMessage());
  }

  @Test
  void shouldNotUpdateToDuplicateName() {
    final var permissionIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    final var dto = UpdateRoleDto.of("new_name", "any_description", RoleStatus.ACTIVE, permissionIds);

    final var role = RoleTestMocks.createActiveTestRole(permissionIds);
    final var id = role.getId();

    when(repository.findById(id)).thenReturn(Optional.of(RoleMapper.entityToJpa(role)));
    when(permissionRepository.countByIdIn(dto.permissionIds())).thenReturn(2L);
    when(repository.existsByName(dto.name())).thenReturn(true);

    final var exception = assertThrows(AlreadyExistsException.class, () -> useCase.execute(id, dto));
    assertEquals("Role already exists", exception.getMessage());
  }

  @Test
  void shouldUpdateWhenOnlyNameChanged() {
    final var id = UUID.randomUUID();
    final var permissionIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    final var dto = UpdateRoleDto.of("new_name", "any_description", RoleStatus.ACTIVE, permissionIds);

    final var role = RoleTestMocks.createActiveTestRole(permissionIds);

    when(repository.findById(id)).thenReturn(Optional.of(RoleMapper.entityToJpa(role)));
    when(permissionRepository.countByIdIn(dto.permissionIds())).thenReturn(2L);

    useCase.execute(id, dto);

    final var roleJpaEntityCaptor = ArgumentCaptor.forClass(RoleJpaEntity.class);
    verify(repository, times(1)).save(roleJpaEntityCaptor.capture());
    final var capturedRole = roleJpaEntityCaptor.getValue();

    assertEquals(dto.name(), capturedRole.getName());
    assertEquals(dto.description(), capturedRole.getDescription());
    assertEquals(dto.status().toString(), capturedRole.getStatus());
    assertEquals(2, capturedRole.getPermissions().size());
    assertTrue(capturedRole.getPermissions().stream().allMatch(p -> dto.permissionIds().contains(p.getId())));
    assertNotNull(capturedRole.getCreatedAt());
    assertNotNull(capturedRole.getUpdatedAt());
  }

  @Test
  void shouldUpdatePermissions() {
    final var id = UUID.randomUUID();
    final var oldPermissionIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    final var newPermissionIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    final var dto = UpdateRoleDto.of("any_name", "any_description", RoleStatus.ACTIVE, newPermissionIds);

    final var role = RoleTestMocks.createActiveTestRole(oldPermissionIds);

    when(repository.findById(id)).thenReturn(Optional.of(RoleMapper.entityToJpa(role)));
    when(permissionRepository.countByIdIn(dto.permissionIds())).thenReturn(2L);

    useCase.execute(id, dto);

    final var roleJpaEntityCaptor = ArgumentCaptor.forClass(RoleJpaEntity.class);
    verify(repository, times(1)).save(roleJpaEntityCaptor.capture());
    final var capturedRole = roleJpaEntityCaptor.getValue();

    assertEquals(dto.name(), capturedRole.getName());
    assertEquals(dto.description(), capturedRole.getDescription());
    assertEquals(dto.status().toString(), capturedRole.getStatus());
    assertEquals(2, capturedRole.getPermissions().size());
    assertTrue(capturedRole.getPermissions().stream().allMatch(p -> dto.permissionIds().contains(p.getId())));
    assertNotNull(capturedRole.getCreatedAt());
    assertNotNull(capturedRole.getUpdatedAt());
  }
}
