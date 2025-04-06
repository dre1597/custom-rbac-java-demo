package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.domain.mocks.RoleTestMocks;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
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
    final var role = RoleTestMocks.createActiveTestRole();

    when(repository.findById(id))
        .thenReturn(Optional.of(RoleMapper.entityToJpa(role)));

    var result = useCase.execute(id);

    assertNotNull(result);
    assertEquals(role.getId(), result.getId());
    assertEquals(role.getName(), result.getName());
    assertEquals(role.getDescription(), result.getDescription());
    assertEquals(role.getStatus(), result.getStatus());
    assertEquals(role.getCreatedAt(), result.getCreatedAt());
    assertEquals(role.getUpdatedAt(), result.getUpdatedAt());
  }

  @Test
  void shouldThrowExceptionWhenRoleNotFound() {
    final var id = UUID.randomUUID();

    when(repository.findById(id))
        .thenReturn(Optional.empty());

    final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(id));

    verify(repository, times(1)).findById(id);
    assertEquals("Role not found", exception.getMessage());
  }
}
