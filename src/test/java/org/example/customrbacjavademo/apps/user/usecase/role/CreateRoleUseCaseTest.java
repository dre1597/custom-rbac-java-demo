package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.domain.dto.NewRoleDto;
import org.example.customrbacjavademo.apps.user.domain.enums.RoleStatus;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.example.customrbacjavademo.common.domain.exceptions.InvalidReferenceException;
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
    var permissionId1 = UUID.randomUUID();
    var permissionId2 = UUID.randomUUID();
    var dto = new NewRoleDto("any_name", "any_description", RoleStatus.ACTIVE, List.of(permissionId1, permissionId2));

    when(repository.existsByName(dto.name())).thenReturn(false);
    when(permissionRepository.countByIdIn(dto.permissionIds())).thenReturn(2L);

    useCase.execute(dto);

    var roleCaptor = ArgumentCaptor.forClass(RoleJpaEntity.class);
    verify(repository, times(1)).save(roleCaptor.capture());
    var savedRole = roleCaptor.getValue();

    assertEquals(dto.name(), savedRole.getName());
    assertEquals(dto.description(), savedRole.getDescription());
    assertEquals(dto.status().toString(), savedRole.getStatus());
    assertEquals(2, savedRole.getPermissions().size());
    assertTrue(savedRole.getPermissions().stream().allMatch(p -> dto.permissionIds().contains(p.getId())));
    assertNotNull(savedRole.getCreatedAt());
    assertNotNull(savedRole.getUpdatedAt());
  }

  @Test
  void shouldNotCreateRoleIfNameAlreadyExists() {
    var dto = new NewRoleDto("any_name", "any_description", RoleStatus.ACTIVE, List.of(UUID.randomUUID()));

    when(repository.existsByName(dto.name())).thenReturn(true);

    var exception = assertThrows(AlreadyExistsException.class, () -> useCase.execute(dto));

    assertEquals("Role already exists", exception.getMessage());
    verify(repository, never()).save(any());
  }

  @Test
  void shouldThrowIfSomePermissionsAreInvalid() {
    var validId = UUID.randomUUID();
    var invalidId = UUID.randomUUID();
    var dto = new NewRoleDto("any_name", "any_descriptions", RoleStatus.ACTIVE, List.of(validId, invalidId));

    when(repository.existsByName(dto.name())).thenReturn(false);
    when(permissionRepository.countByIdIn(dto.permissionIds())).thenReturn(1L);

    var exception = assertThrows(InvalidReferenceException.class, () -> useCase.execute(dto));

    assertEquals("Some permissions are invalid or missing. Provided: " + dto.permissionIds(), exception.getMessage());
    verify(repository, never()).save(any());
  }
}
