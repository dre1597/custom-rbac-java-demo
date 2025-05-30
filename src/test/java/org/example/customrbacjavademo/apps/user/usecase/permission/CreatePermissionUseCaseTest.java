package org.example.customrbacjavademo.apps.user.usecase.permission;

import org.example.customrbacjavademo.apps.user.domain.dto.NewPermissionDto;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionName;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionScope;
import org.example.customrbacjavademo.apps.user.domain.enums.PermissionStatus;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaEntity;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.common.domain.exceptions.AlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePermissionUseCaseTest {
  @Mock
  private PermissionJpaRepository repository;

  @InjectMocks
  private CreatePermissionUseCase useCase;

  @Test
  void shouldCreatePermission() {
    final var dto = NewPermissionDto.of(
        PermissionName.READ.name(),
        PermissionScope.USER.name(),
        "any_description",
        PermissionStatus.ACTIVE.name()
    );

    when(repository.existsByNameAndScope(dto.name(), dto.scope()))
        .thenReturn(false);

    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    useCase.execute(dto);

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
  void shouldNotCreatePermissionWithSameNameAndScopeTogether() {
    final var dto = NewPermissionDto.of(
        PermissionName.READ.name(),
        PermissionScope.USER.name(),
        "any_description",
        PermissionStatus.ACTIVE.name()
    );

    when(repository.existsByNameAndScope(dto.name(), dto.scope()))
        .thenReturn(true);

    final var exception = org.junit.jupiter.api.Assertions.assertThrows(AlreadyExistsException.class, () -> useCase.execute(dto));
    assertEquals("Permission already exists", exception.getMessage());
  }
}
