package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRoleUseCaseTest {
  @Mock
  private RoleJpaRepository repository;

  @InjectMocks
  private DeleteRoleUseCase useCase;

  @Test
  void shouldDeleteRole() {
    final var id = UUID.randomUUID();

    when(repository.existsById(id)).thenReturn(true);

    useCase.execute(id);

    verify(repository, times(1)).deleteById(id);
  }

  @Test
  void shouldDoNothingWhenRoleDoesNotExist() {
    final var id = UUID.randomUUID();

    when(repository.existsById(id)).thenReturn(false);
    
    useCase.execute(id);

    verify(repository, never()).deleteById(id);
  }
}
